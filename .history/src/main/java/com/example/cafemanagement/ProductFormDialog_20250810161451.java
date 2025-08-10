// inside ProductFormDialog
private JTextField imageField;
private JButton chooseImageBtn;

private void initUI() {
    JPanel form = new JPanel(new GridLayout(5, 2, 8, 8));

    JTextField nameField = new JTextField();
    JTextField priceField = new JTextField();
    JComboBox<String> categoryCombo = new JComboBox<>(new String[]{"Coffee", "Snacks", "Dessert", "Beverage"});
    imageField = new JTextField();
    imageField.setEditable(false);

    chooseImageBtn = new JButton("Choose Image");
    chooseImageBtn.addActionListener(e -> chooseImage());

    form.add(new JLabel("Name:"));
    form.add(nameField);
    form.add(new JLabel("Price:"));
    form.add(priceField);
    form.add(new JLabel("Category:"));
    form.add(categoryCombo);
    form.add(new JLabel("Image:"));
    form.add(imageField);
    form.add(new JLabel());
    form.add(chooseImageBtn);

    add(form, BorderLayout.CENTER);
}

private void chooseImage() {
    JFileChooser chooser = new JFileChooser();
    if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
        java.io.File selectedFile = chooser.getSelectedFile();

        try {
            // Ensure images folder exists
            java.io.File imagesDir = new java.io.File("images");
            if (!imagesDir.exists()) imagesDir.mkdirs();

            // Copy file to images folder
            java.io.File dest = new java.io.File(imagesDir, selectedFile.getName());
            java.nio.file.Files.copy(selectedFile.toPath(), dest.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            // Save relative path
            imageField.setText("images/" + selectedFile.getName());
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving image: " + ex.getMessage());
        }
    }
}

// When saving product
public Product getProduct() {
    return new Product(
        null,
        nameField.getText(),
        Double.parseDouble(priceField.getText()),
        (String) categoryCombo.getSelectedItem(),
        imageField.getText() // save path
    );
}
