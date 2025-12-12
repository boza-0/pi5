-- Trigger: After INSERT on order_products
CREATE DEFINER=`shopuser`@`localhost` TRIGGER trg_order_products_after_insert
AFTER INSERT ON order_products
FOR EACH ROW
BEGIN
  UPDATE orders
  SET subtotal_amount = (
        SELECT COALESCE(SUM(line_total),0)
        FROM order_products
        WHERE order_id = NEW.order_id
      ),
      tax_amount = subtotal_amount * 0.21,
      total_amount = GREATEST(subtotal_amount - discount_amount + tax_amount, 0)
  WHERE id = NEW.order_id;
END;

-- Trigger: After UPDATE on order_products
CREATE DEFINER=`shopuser`@`localhost` TRIGGER trg_order_products_after_update
AFTER UPDATE ON order_products
FOR EACH ROW
BEGIN
  UPDATE orders
  SET subtotal_amount = (
        SELECT SUM(line_total)
        SELECT COALESCE(SUM(line_total),0)
        FROM order_products
        WHERE order_id = NEW.order_id
      ),
      tax_amount = subtotal_amount * 0.21,
      total_amount = subtotal_amount - discount_amount + tax_amount
      total_amount = GREATEST(subtotal_amount - discount_amount + tax_amount, 0)
  WHERE id = NEW.order_id;
END;

-- Trigger: After DELETE on order_products
CREATE DEFINER=`shopuser`@`localhost` TRIGGER trg_order_products_after_delete
AFTER DELETE ON order_products
FOR EACH ROW
BEGIN
  UPDATE orders
  SET subtotal_amount = (
        SELECT COALESCE(SUM(line_total),0)
        FROM order_products
        WHERE order_id = OLD.order_id
      ),
      tax_amount = subtotal_amount * 0.21,
      total_amount = subtotal_amount - discount_amount + tax_amount
      total_amount = GREATEST(subtotal_amount - discount_amount + tax_amount, 0)
  WHERE id = OLD.order_id;
END;
