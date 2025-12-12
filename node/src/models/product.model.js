// product.model.js
const db = require('../config/db');

const Product = {
  getAll: async () => {
    const [rows] = await db.query('SELECT * FROM products');
    return rows;
  },

  getById: async (id) => {
    const [rows] = await db.query('SELECT * FROM products WHERE id = ?', [id]);
    return rows[0];
  },

  create: async (product) => {
    const { name, description, price, stock, provider_id } = product;
    const [result] = await db.query(
      'INSERT INTO products (name, description, price, stock, provider_id) VALUES (?, ?, ?, ?, ?)',
      [name, description, price, stock, provider_id]
    );
    return Product.getById(result.insertId);
  },

  update: async (id, product) => {
    const { name, description, price, stock, provider_id } = product;
    await db.query(
      'UPDATE products SET name=?, description=?, price=?, stock=?, provider_id=? WHERE id=?',
      [name, description, price, stock, provider_id, id]
    );
    return Product.getById(id);
  },

  delete: async (id) => {
    await db.query('DELETE FROM products WHERE id=?', [id]);
  }
};

module.exports = Product;
