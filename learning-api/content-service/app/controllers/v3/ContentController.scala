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

    def getCacheData() = {
        def fetchData(key:String, objKey:String):String = {"Sample Data...."}
        def fetchListData(key:String, objKey:String):List[String] = {List("val1","val2","val3")}
        val d = ContentCache.getObject("test",fetchData)
        val d1 = ContentCache.getObject("testHandler",fetchData)
        val d2 = ContentCache.getObject("testHandler")

        println("data at controller : "+d)
        println("data from handler at controller : "+d1)
        println("data from default handler : "+d2)
        println("--------------------------------------------------")
        println("Saving List Data To Redis .... ")
        val in = List[String]("data1","data2","data3")
        ContentCache.setList("testList",in)
        println("List Data Saved Successfully!")
        println("Reading List Data....")
        val dl = ContentCache.getList("testList",fetchListData)
        println("redis data for key testList :: "+dl)
        val dlH = ContentCache.getList("testListH",fetchListData)
        println("handler data for key testListH :: "+dlH)
    }

    def create() = Action.async { implicit request =>
        val headers = commonHeaders()
        getCacheData()
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

}
