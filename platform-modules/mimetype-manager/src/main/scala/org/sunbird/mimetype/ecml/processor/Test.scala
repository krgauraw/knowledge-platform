package org.sunbird.mimetype.ecml.processor

import java.io.File

import com.mashape.unirest.http.Unirest

object Test {

	def main(args: Array[String]): Unit = {
		//val mediaSrc = "assets/public/content/do_113041451668733952127/artifact/screenshot-from-2020-06-08-11-18-40_1591974084062.png"
		//val mediaSrc = "content-plugins/org.ekstep.navigation-1.0/renderer/controller/navigation_ctrl.js"
		val mediaSrc = "assets/public/content/do_113041450774429696125/artifact/1591974058580.thumb.png"
		val baseUrl = "https://dev.sunbirded.org"
		/*val blobUrl = if(mediaSrc.startsWith("/assets/public/")) baseUrl + mediaSrc else if(mediaSrc.startsWith("/") && mediaSrc.startsWith("assets/public/"))baseUrl+"/"+mediaSrc
		else baseUrl + "/assets/public/" + mediaSrc*/
		val blobUrl = if(mediaSrc.startsWith("assets/public/")) baseUrl +File.separator+ mediaSrc else baseUrl + File.separator + "assets/public/"+mediaSrc
		println("blobUrl ::: "+blobUrl)
		println("flag ::: "+mediaSrc.startsWith("/assets/public/"))
		println(getBlobLength(blobUrl))
	}


	def getBlobLength(url:String):Long = {
		val response = Unirest.head(url).header("Content-Type", "application/json").asString
		if (response.getStatus == 200 && null!=response.getHeaders) {
			val size : Long = if(response.getHeaders.containsKey("Content-Length")) response.getHeaders.get("Content-Length").get(0).toLong
			else if(response.getHeaders.containsKey("content-length")) response.getHeaders.get("content-length").get(0).toLong else 0.toLong
			size
		} else 0.toLong
	}
}
