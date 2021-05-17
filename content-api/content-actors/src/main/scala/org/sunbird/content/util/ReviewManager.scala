package org.sunbird.content.util

import java.io.File
import java.util

import org.apache.commons.lang3.StringUtils
import org.sunbird.common.dto.{Request, Response, ResponseHandler}
import org.sunbird.common.exception.ResponseCode
import org.sunbird.content.upload.mgr.UploadManager
import org.sunbird.content.upload.mgr.UploadManager.{MEDIA_TYPE_LIST, getUploadResponse, pushInstructionEvent}
import org.sunbird.graph.OntologyEngineContext
import org.sunbird.graph.dac.model.Node
import org.sunbird.graph.nodes.DataNode
import org.sunbird.mimetype.factory.MimeTypeManagerFactory
import org.sunbird.models.UploadParams

import scala.collection.JavaConversions.mapAsJavaMap
import scala.collection.Map
import scala.concurrent.{ExecutionContext, Future}

object ReviewManager {

	def review(request: Request)(implicit oec: OntologyEngineContext, ec: ExecutionContext): Future[Response] = {
		val identifier: String = request.getContext.getOrDefault("identifier", "").asInstanceOf[String]
		val readReq = new Request(request)
		readReq.put("identifier", identifier)
		readReq.put("fields", new util.ArrayList[String])
		DataNode.read(readReq).map(node => {
			if (null != node & StringUtils.isNotBlank(node.getObjectType))
				request.getContext.put("schemaName", node.getObjectType.toLowerCase())
			UploadManager.upload(request, node)
		}).flatMap(f => f)

		val identifier: String = node.getIdentifier
		val fileUrl: String = request.getRequest.getOrDefault("fileUrl", "").asInstanceOf[String]
		val file = request.getRequest.get("file").asInstanceOf[File]
		val reqFilePath: String = request.getRequest.getOrDefault("filePath", "").asInstanceOf[String].replaceAll("^/+|/+$", "")
		val filePath = if(StringUtils.isBlank(reqFilePath)) None else Option(reqFilePath)
		val mimeType = node.getMetadata().getOrDefault("mimeType", "").asInstanceOf[String]
		val mediaType = node.getMetadata.getOrDefault("mediaType", "").asInstanceOf[String]
		val mgr = MimeTypeManagerFactory.getManager(node.getObjectType, mimeType)
		val params: UploadParams = request.getContext.get("params").asInstanceOf[UploadParams]
		val uploadFuture: Future[Map[String, AnyRef]] = if (StringUtils.isNotBlank(fileUrl)) mgr.upload(identifier, node, fileUrl, filePath, params) else mgr.upload(identifier, node, file, filePath, params)
		uploadFuture.map(result => {
			if(filePath.isDefined)
				updateNode(request, node.getIdentifier, mediaType, node.getObjectType, result + (ContentConstants.ARTIFACT_BASE_PATH -> filePath.get))
			else
				updateNode(request, node.getIdentifier, mediaType, node.getObjectType, result)
		}).flatMap(f => f)
	}

	def updateNode(request: Request, identifier: String, mediaType: String, objectType: String, result: Map[String, AnyRef])(implicit oec: OntologyEngineContext, ec: ExecutionContext): Future[Response] = {
		val updatedResult = result - "identifier"
		val artifactUrl = updatedResult.getOrElse("artifactUrl", "").asInstanceOf[String]
		val size: Double = updatedResult.getOrElse("size", 0.asInstanceOf[Double]).asInstanceOf[Double]
		if (StringUtils.isNotBlank(artifactUrl)) {
			val updateReq = new Request(request)
			updateReq.getContext().put("identifier", identifier)
			updateReq.getRequest.putAll(mapAsJavaMap(updatedResult))
			if( size > CONTENT_ARTIFACT_ONLINE_SIZE)
				updateReq.put("contentDisposition", "online-only")
			if (StringUtils.equalsIgnoreCase("Asset", objectType) && MEDIA_TYPE_LIST.contains(mediaType))
				updateReq.put("status", "Processing")

			DataNode.update(updateReq).map(node => {
				if (StringUtils.equalsIgnoreCase("Asset", objectType) && MEDIA_TYPE_LIST.contains(mediaType) && null != node)
					pushInstructionEvent(identifier, node)
				getUploadResponse(node)
			})
		} else {
			Future {
				ResponseHandler.ERROR(ResponseCode.SERVER_ERROR, "ERR_UPLOAD_FILE", "Something Went Wrong While Processing Your Request.")
			}
		}
	}
}
