package helpers

import views.html.helper._

object Bootstrap {
    
  implicit val myFields = FieldConstructor(views.html.common.fieldConstructor.f)    
    
}
