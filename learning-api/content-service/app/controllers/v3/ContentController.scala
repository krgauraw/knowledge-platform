package controllers.v3

import akka.actor.{ActorRef, ActorSystem}
import cache.ContentCache
import com.google.inject.Singleton
import controllers.BaseController
import javax.inject.{Inject, Named}
import org.sunbird.telemetry.logger.TelemetryManager
import play.api.mvc.ControllerComponents
import utils.{ActorNames, ApiId}

import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext

@Singleton
class ContentController @Inject()(@Named(ActorNames.CONTENT_ACTOR) contentActor: ActorRef,@Named(ActorNames.COLLECTION_ACTOR) collectionActor: ActorRef, cc: ControllerComponents, actorSystem: ActorSystem)(implicit exec: ExecutionContext) extends BaseController(cc) {

    val objectType = "Content"
    val schemaName: String = "content"
    val version = "1.0"

    def create() = Action.async { implicit request =>
        val headers = commonHeaders()
        testContentCache()
        val body = requestBody()
        val content = body.getOrElse("content", new java.util.HashMap()).asInstanceOf[java.util.Map[String, Object]];
        content.putAll(headers)
        val contentRequest = getRequest(content, headers, "createContent")
        setRequestContext(contentRequest, version, objectType, schemaName)
        getResult(ApiId.CREATE_CONTENT, contentActor, contentRequest)
    }
  
    /**
      * This Api end point takes 3 parameters
      * Content Identifier the unique identifier of a content
      * Mode in which the content can be viewed (default read or edit)
      * Fields are metadata that should be returned to visualize
      * @param identifier
      * @param mode
      * @param fields
      * @return
      */
    def read(identifier: String, mode: Option[String], fields: Option[String]) = Action.async { implicit request =>
        val headers = commonHeaders()
        val content = new java.util.HashMap().asInstanceOf[java.util.Map[String, Object]]
        content.putAll(headers)
        content.putAll(Map("identifier" -> identifier, "mode" -> mode.getOrElse("read"), "fields" -> fields.getOrElse("")).asInstanceOf[Map[String, Object]])
        val readRequest = getRequest(content, headers, "readContent")
        setRequestContext(readRequest, version, objectType, schemaName)
        getResult(ApiId.READ_CONTENT, contentActor, readRequest)
    }

    def update(identifier:String) = Action.async { implicit request =>
        val headers = commonHeaders()
        val body = requestBody()
        val content = body.getOrElse("content", new java.util.HashMap()).asInstanceOf[java.util.Map[String, Object]];
        content.putAll(headers)
        val contentRequest = getRequest(content, headers, "updateContent")
        setRequestContext(contentRequest, version, objectType, schemaName)
        contentRequest.getContext.put("identifier",identifier);
        getResult(ApiId.UPDATE_CONTENT, contentActor, contentRequest)
    }

    def addHierarchy() = Action.async { implicit request =>
        val headers = commonHeaders()
        val body = requestBody()
        body.putAll(headers)
        val contentRequest = getRequest(body, headers, "addHierarchy")
        setRequestContext(contentRequest, version, objectType, schemaName)
        getResult(ApiId.ADD_HIERARCHY, collectionActor, contentRequest)
    }

    def removeHierarchy() = Action.async { implicit request =>
        val headers = commonHeaders()
        val body = requestBody()
        body.putAll(headers)
        val contentRequest = getRequest(body, headers, "removeHierarchy")
        setRequestContext(contentRequest, version, objectType, schemaName)
        getResult(ApiId.REMOVE_HIERARCHY, collectionActor, contentRequest)
    }

    def testContentCache() = {
        println("============ Content Cache Testing ============")

        println("Saving String Data For Key test-c-001")
        ContentCache.setObject("test-c-001","Hello! I am from Cache!",0)

        println("Getting String Data For Key test-c-001 With Default Handler")
        println("Data for Key test-c-001 : "+ContentCache.getObject("test-c-001"))

        println("Getting String Data For Key test-c-002 With Default Handler")
        println("Data for Key test-c-002 : "+ContentCache.getObject("test-c-002"))

        def customHandler(key:String, objKey:String):String = "I am from Custom Handler!"
        println("Getting String Data For Key test-c-003 With Custom Handler")
        println("Data for Key test-c-003 : "+ContentCache.getObject("test-c-003",customHandler))

        println("\n------------------------------------------------------------------------------------\n")

        println("Saving List Data For Key test-cl-001")
        ContentCache.setList("test-cl-001", List[String]("test-l-val1","test-l-val2"))

        println("Reading List Data For Key test-cl-001 With Default Handler")
        println("Data for Key test-cl-001 : "+ContentCache.getList("test-cl-001"))

        println("Reading List Data For Key test-cl-002 With Default Handler")
        println("Data for Key test-cl-002 : "+ContentCache.getList("test-cl-002"))

        def customListHandler(key:String, objKey:String):List[String] = List[String]("cache-custom-handler-val")
        println("Reading List Data For Key test-cl-003 With Custom Handler")
        println("Data for Key test-cl-003 : "+ContentCache.getList("test-cl-003",customListHandler))

    }

}
