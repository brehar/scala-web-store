package services

import mocks.ProductMockedDao
import models.Product
import org.scalatestplus.play.PlaySpec
import utils.Awaits

class ProductServiceTestSpec extends PlaySpec {
  "ProductService" must {
    val service: ProductService = new ProductServiceImpl(new ProductMockedDao)

    "insert a product properly" in {
      val product = Product(Some(1), "Ball", "Awesome Basketball", 19.75)
      service.insert(product)
    }

    "update a product" in {
      val product = Product(Some(1), "Blue Ball", "Awesome Blue Basketball", 19.99)
      service.update(1, product)
    }

    "not update because does not exist" in {
      intercept[RuntimeException] {
        Awaits.get(5, service.update(333, null))
      }
    }

    "find the product 1" in {
      val product = Awaits.get(5, service.findById(1))
      product.get.id mustBe Some(1)
      product.get.name mustBe "Blue Ball"
      product.get.details mustBe "Awesome Blue Basketball"
      product.get.price mustBe 19.99
    }

    "find all" in {
      val products = Awaits.get(5, service.findAll())
      products.get.length mustBe 1
      products.get.head.id mustBe Some(1)
      products.get.head.name mustBe "Blue Ball"
      products.get.head.details mustBe "Awesome Blue Basketball"
      products.get.head.price mustBe 19.99
    }

    "find all products" in {
      val products = service.findAllProducts()
      products.length mustBe 1
      products.head._1 mustBe "1"
      products.head._2 mustBe "Blue Ball"
    }

    "remove 1 product" in {
      val product = Awaits.get(5, service.remove(1))
      product mustBe 1

      val oldProduct = Awaits.get(5, service.findById(1))
      oldProduct mustBe None
    }

    "not remove because does not exist" in {
      intercept[RuntimeException] {
        Awaits.get(5, service.remove(-1))
      }
    }
  }
}
