import java.time.Clock

import com.google.inject.AbstractModule
import dao._
import services._

class Module extends AbstractModule {
  def configure(): Unit = {
    bind(classOf[Clock]).toInstance(Clock.systemDefaultZone())
    bind(classOf[ApplicationTimer]).asEagerSingleton()
    bind(classOf[ProductService]).to(classOf[ProductServiceImpl]).asEagerSingleton()
    bind(classOf[ReviewService]).to(classOf[ReviewServiceImpl]).asEagerSingleton()
    bind(classOf[ImageService]).to(classOf[ImageServiceImpl]).asEagerSingleton()
    bind(classOf[PriceService]).to(classOf[PriceServiceImpl]).asEagerSingleton()
    bind(classOf[RndService]).to(classOf[RndServiceImpl]).asEagerSingleton()
    bind(classOf[ProductDao]).to(classOf[ProductDaoImpl]).asEagerSingleton()
    bind(classOf[ImageDao]).to(classOf[ImageDaoImpl]).asEagerSingleton()
    bind(classOf[ReviewDao]).to(classOf[ReviewDaoImpl]).asEagerSingleton()
  }
}
