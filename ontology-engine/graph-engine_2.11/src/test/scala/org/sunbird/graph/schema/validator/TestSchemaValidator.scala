package org.sunbird.graph.schema.validator

import java.util

import org.sunbird.graph.BaseSpec
import org.sunbird.graph.dac.model.Node
import org.sunbird.graph.schema.DefinitionFactory

import scala.concurrent.Future

class TestSchemaValidator extends BaseSpec {

  /*"check health api" should "return true" in {
    val future: Future[Response] = HealthCheckManager.checkAllSystemHealth()
    future map { response => {
      assert(ResponseCode.OK == response.getResponseCode)
      assert(response.get("healthy") == true)
    }
    }
  }*/

  "check schemaValidate api" should "return true" in {
    val definition = DefinitionFactory.getDefinition("domain", "collection", "1.0")

    val a = new util.ArrayList[AnyRef](){{ add(new util.HashMap[String, AnyRef](){{
      put("name","abc")
    }}) }}

    val metaData = new util.HashMap[String, AnyRef](){{
      put("name","abc")
      put("code", "code")
      put("contentType", "TextBook")
      put("mimeType", "application/vnd.ekstep.content-collection")
      put("channel", "in.ekstep")
      put("contentCredits", a)
    }}


    val node: Node = new Node("abc", "DATA_NODE", "Content");
    node.setGraphId("domain")
    node.setMetadata(metaData)

    val future: Future[Node] = definition.validate(node, "create")
    future map { node => {
      assert(null != node)
    }
    }
  }


  "check schemaValidate api for category" should "return true" in {
    val definition = DefinitionFactory.getDefinition("domain", "category", "1.0")
    val metaData = new util.HashMap[String, AnyRef](){{
      put("name","Test Category")
      put("identifier", "cat-test-1")
      put("status", "Live")
      put("objectTypes", "Test")
    }}

    val node: Node = new Node("cat-test-1", "DATA_NODE", "Category");
    node.setGraphId("domain")
    node.setMetadata(metaData)

    val future: Future[Node] = definition.validate(node, "create")
    future map { node => {
      println("node metadata ::: "+node.getMetadata)
      assert(null != node)
    }
    }
  }
}
