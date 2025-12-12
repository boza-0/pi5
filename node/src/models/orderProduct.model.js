// src/models/orderProduct.model.js
const db = require('../config/db');

const OrderProduct = {
  getByOrderId: async (orderId) => {
    const [rows] = await db.query(
      'SELECT * FROM order_products WHERE order_id = ?',
      [orderId]
    );
    return rows;
  },

  getById: async (orderId, productId) => {
    const [rows] = await db.query(
      'SELECT * FROM order_products WHERE order_id = ? AND id = ?',
      [orderId, productId]
    );
    return rows[0];
  },

  add: async (orderId, product) => {
    const { product_id, quantity, unit_price } = product;
    const [result] = await db.query(
      'INSERT INTO order_products (order_id, product_id, quantity, unit_price) VALUES (?, ?, ?, ?)',
      [orderId, product_id, quantity, unit_price]
    );
    return OrderProduct.getById(orderId, result.insertId);
  },

  update: async (orderId, productId, product) => {
    const { product_id, quantity, unit_price } = product;
    await db.query(
      'UPDATE order_products SET product_id=?, quantity=?, unit_price=? WHERE order_id=? AND id=?',
      [product_id, quantity, unit_price, orderId, productId]
    );
    return OrderProduct.getById(orderId, productId);
  },

  delete: async (orderId, productId) => {
    await db.query(
      'DELETE FROM order_products WHERE order_id = ? AND id = ?',
      [orderId, productId]
    );
  }
};

module.exports = OrderProduct;
