package com.example.cafemanagement;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class OrderPanel extends JPanel {
    private ProductDAO productDAO;
    private OrderDAO orderDAO;
    private JTable menuTable; // products
    private DefaultTableModel menuModel;
    private JTable cartTable;
    private DefaultTableModel cartModel;
    private List<CartItem> cart = new ArrayList<>();

    public OrderPanel() {
        productDAO = new ProductDAO();
        orderDAO = new OrderDAO();
        setLayout(new BorderLayout(8,8));

        // Left: menu
        String[] menuCols = {"ID","Name","Price","Stock","QtyToAdd"};
        menuModel = new DefaultTableModel(menuCols, 0) {
            @Override public boolean isCellEditable(int r,int c){ return c==4; }
        };
        menuTable = new JTable(menuModel);
        menuTable.getColumnModel().getColumn(0).setMinWidth(0);
        menuTable.getColumnModel().getColumn(0).setMaxWidth(0);
        JScrollPane left = new JScrollPane(menuTable);
        left.setPreferredSize(new Dimension(400, 0));
        add(left, BorderLayout.WEST);

        // Right: cart & actions
        JPanel right = new JPanel(new BorderLayout(6,6));

        String[] cartCols = {"ProductId","Name","Price","Qty","Subtotal"};
        cartModel = new DefaultTableModel(cartCols,0) {
            @Override public boolean isCellEditable(int r,int c){return false;}
        };
        cartTable = new JTable(cartModel);
        cartTable.getColumnModel().getColumn(0).setMinWidth(0);
        cartTable.getColumnModel().getColumn(0).setMaxWidth(0);
        right.add(new JScrollPane(cartTable), BorderLayout.CENTER);

        JPanel actions = new JPanel();
        JButton addBtn = new JButton("Add to Cart");
        addBtn.addActionListener(e -> addSelectedToCart());
        JButton removeBtn = new JButton("Remove Item");
        removeBtn.addActionListener(e -> removeSelectedFromCart());
        JButton placeBtn = new JButton("Place Order");
        placeBtn.addActionListener(e -> placeOrder());
        JButton refresh = new JButton("Refresh Menu");
        refresh.addActionListener(e -> loadMenu());
        actions.add(addBtn); actions.add(removeBtn); actions.add(placeBtn); actions.add(refresh);
        right.add(actions, BorderLayout.SOUTH);

        add(right, BorderLayout.CENTER);

        loadMenu();
    }

    private void loadMenu() {
        menuModel.setRowCount(0);
        List<Product> products = productDAO.getAllProducts();
        for (Product p : products) {
            menuModel.addRow(new Object[]{p.getId(), p.getName(), p.getPrice(), p.getStock(), 1});
        }
    }

    private void addSelectedToCart() {
        int row = menuTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select an item from menu"); return; }
        String id = (String) menuModel.getValueAt(row, 0);
        String name = (String) menuModel.getValueAt(row, 1);
        double price = ((Number)menuModel.getValueAt(row, 2)).doubleValue();
        int stock = ((Number)menuModel.getValueAt(row, 3)).intValue();
        int qty;
        Object qtyObj = menuModel.getValueAt(row, 4);
        if (qtyObj instanceof Number) qty = ((Number) qtyObj).intValue();
        else qty = Integer.parseInt(qtyObj.toString());

        if (qty <= 0) { JOptionPane.showMessageDialog(this, "Quantity must be >=1"); return; }
        if (qty > stock) { JOptionPane.showMessageDialog(this, "Not enough stock"); return; }

        // if already in cart, increase qty
        for (CartItem it : cart) {
            if (it.getProductId().equals(id)) {
                it = new CartItem(it.getProductId(), it.getName(), it.getPrice(), it.getQty() + qty);
                rebuildCartFromList();
                return;
            }
        }

        CartItem item = new CartItem(id, name, price, qty);
        cart.add(item);
        rebuildCartFromList();
    }

    private void rebuildCartFromList() {
        cartModel.setRowCount(0);
        for (CartItem it : cart) {
            cartModel.addRow(new Object[]{it.getProductId(), it.getName(), it.getPrice(), it.getQty(), it.subtotal()});
        }
    }

    private void removeSelectedFromCart() {
        int r = cartTable.getSelectedRow();
        if (r == -1) { JOptionPane.showMessageDialog(this, "Select cart item"); return; }
        String id = (String) cartModel.getValueAt(r, 0);
        cart.removeIf(it -> it.getProductId().equals(id));
        rebuildCartFromList();
    }

    private void placeOrder() {
        if (cart.isEmpty()) { JOptionPane.showMessageDialog(this, "Cart is empty"); return; }
        // attempt to place order (decrement stock atomically)
        boolean ok = orderDAO.placeOrderAndSave(cart);
        if (!ok) {
            JOptionPane.showMessageDialog(this, "Order failed - stock insufficient for one or more items. Refresh menu and try again.");
            loadMenu();
            return;
        }
        double total = 0;
        for (CartItem it : cart) total += it.subtotal();
        JOptionPane.showMessageDialog(this, "Order placed! Total: ₹" + total);
        cart.clear();
        rebuildCartFromList();
        loadMenu();
    }
}
