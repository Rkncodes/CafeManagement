private JLabel dropLabel;
private String savedImagePath;

private void initUI() {
    JPanel form = new JPanel(new GridLayout(5, 2, 8, 8));

    JTextField nameField = new JTextField();
    JTextField priceField = new JTextField();
    JComboBox<String> categoryCombo = new JComboBox<>(new String[]{"Coffee", "Snacks", "Dessert", "Beverage"});

    // Drag & Drop Label
    dropLabel = new JLabel("Drop Image Here", SwingConstants.CENTER);
    dropLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
    dropLabel.setPreferredSize(new Dimension(200, 150));

    // Enable Drop
    dropLabel.setTransferHandler(new TransferHandler() {
        @Override
        public boolean canImport(TransferSupport support) {
            return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
        }

        @Override
        public boolean importData(TransferSupport support) {
            try {
                java.util.List<File> files = (java.util.List<File>) support.getTransferable()
                        .getTransferData(DataFlavor.javaFileListFlavor);
                if (!files.isEmpty()) {
                    File file = files.get(0);
                    // Copy to images folder
                    File imagesDir = new File("images");
                    if (!imagesDir.exists()) imagesDir.mkdirs();

                    File dest = new File(imagesDir, file.getName());
                    Files.copy(file.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);

                    // Save path
                    savedImagePath = "images/" + file.getName();
                    dropLabel.setText("<html><center>Image Added:<br>" + file.getName() + "</center></html>");

                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    });

    form.add(new JLabel("Name:"));
    form.add(nameField);
    form.add(new JLabel("Price:"));
    form.add(priceField);
    form.add(new JLabel("Category:"));
    form.add(categoryCombo);
    form.add(new JLabel("Image:"));
    form.add(dropLabel);

    add(form, BorderLayout.CENTER);

    // Save Button
    JButton saveBtn = new JButton("Save");
    saveBtn.addActionListener(e -> {
        Product p = new Product(null, nameField.getText(),
                Double.parseDouble(priceField.getText()),
                (String) categoryCombo.getSelectedItem(),
                savedImagePath);
        productDAO.insert(p);
        dispose();
    });
    add(saveBtn, BorderLayout.SOUTH);
}
