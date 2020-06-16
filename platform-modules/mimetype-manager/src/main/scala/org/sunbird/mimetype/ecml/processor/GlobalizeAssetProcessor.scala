package org.sunbird.mimetype.ecml.processor

import java.io.File

import com.mashape.unirest.http.{HttpResponse, Unirest}
import org.apache.commons.collections.MapUtils
import org.apache.commons.io.FilenameUtils
import org.apache.commons.lang3.StringUtils
import org.sunbird.cloudstore.StorageService
import org.sunbird.common.{HttpUtil, Platform, Slug}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

trait GlobalizeAssetProcessor extends IProcessor {

    val ASSET_DIR:String = "cloud_storage.asset.folder"
    val OBJECT_DIR:String = "cloud_storage.content.folder"
    val timeout: Long = if(Platform.config.hasPath("asset.max_upload_time")) Platform.config.getLong("asset.max_upload_time") else 60

    abstract override def process(ecrf: Plugin)(implicit ss: StorageService): Plugin = {
        println("GlobalizeAssetProcessor ::: process ::: start")
        val manifest = ecrf.manifest
        println("manifest :::: "+manifest)
        val updatedMedias:List[Media] = uploadAssets(manifest.medias)
        println("updatedMedias :::: "+updatedMedias)
        val updatedManifest:Manifest = Manifest(manifest.id, manifest.data, manifest.innerText, manifest.cData, updatedMedias)
        println("updatedManifest :::: "+updatedManifest)
        super.process(Plugin(ecrf.id, ecrf.data, ecrf.innerText, ecrf.cData, ecrf.childrenPlugin, updatedManifest, ecrf.controllers, ecrf.events))
    }

    def uploadAssets(medias: List[Media])(implicit ss: StorageService, ec: ExecutionContext =  concurrent.ExecutionContext.Implicits.global): List[Media] = {
        if(null != medias) {
            val future:Future[List[Media]] = Future.sequence(medias.filter(media=> StringUtils.isNotBlank(media.id) && StringUtils.isNotBlank(media.src) && StringUtils.isNotBlank(media.`type`))
                    .map(media => {
                        //println("media :::: "+media)

                        Future{
                            println("media src ::: "+media.src)
                            val file:File  = {
                                if(widgetTypeAssets.contains(media.`type`)) new File(getBasePath() + File.separator + "widgets" + File.separator + media.src)
                                else new File(getBasePath() + File.separator + "assets" + File.separator + media.src)
                            }
                            if(null!=file)
                            println("file path ::: "+file.getAbsolutePath)
                            /*val cloudDirName = {
                                val assetDir = if(Platform.config.hasPath(ASSET_DIR)) Platform.config.getString(ASSET_DIR) else System.currentTimeMillis()
                                Platform.config.getString(OBJECT_DIR) + File.separator + Slug.makeSlug(getIdentifier(), true) + File.separator + assetDir
                            }*/
                            val mediaSrc = media.data.getOrElse("src", "").asInstanceOf[String]
                            println("mediaSrc :::: "+mediaSrc)
                            val cloudDirName = FilenameUtils.getFullPathNoEndSeparator(mediaSrc)
                            println("cloudDirName ::: "+cloudDirName)
                            //TODO: take it from config
                            val baseUrl = "https://dev.sunbirded.org"
                            val blobUrl = if(mediaSrc.startsWith("/assets/public/")) baseUrl + mediaSrc else if(mediaSrc.startsWith("/"))baseUrl+mediaSrc
                                else baseUrl + File.separator+mediaSrc
                            println("blobUrl :::: "+blobUrl)

                            val uploadFileUrl: Array[String] = if(StringUtils.isNoneBlank(cloudDirName) && getBlobLength(media.src)==0) ss.uploadFile(cloudDirName, file)
                                else new Array[String](1)

                            //val uploadFileUrl: Array[String] = ss.uploadFile(cloudDirName, file)
                            println("uploadFileUrl :::: "+uploadFileUrl.toList)
                            /*if(null != uploadFileUrl && uploadFileUrl.size > 1) {
                                val url = uploadFileUrl(0)
                                if(!(url.startsWith("http") || url.startsWith("/"))){
                                   val temp =  media.data ++ Map("src" -> ("/" + url))
                                    Media(media.id, temp, media.innerText, media.cData, uploadFileUrl(1), media.`type`, media.childrenPlugin)
                                }else
                                Media(media.id, media.data, media.innerText, media.cData, uploadFileUrl(1), media.`type`, media.childrenPlugin)
                            } else media*/

                            if(null != uploadFileUrl && uploadFileUrl.size > 1) {
                                val src = media.data.getOrElse("src", "").asInstanceOf[String]
                                if(!(src.startsWith("http") || src.startsWith("/"))) {
                                    val temp =  media.data ++ Map("src" -> ("/" + src))
                                    //val temp =  media.data ++ Map("src" -> uploadFileUrl(1))
                                    //val temp =  media.data ++ Map("src" -> ("/assets/public/"+uploadFileUrl(0)))
                                    Media(media.id, temp, media.innerText, media.cData, uploadFileUrl(1), media.`type`, media.childrenPlugin)
                                }else
                                    Media(media.id, media.data, media.innerText, media.cData, uploadFileUrl(1), media.`type`, media.childrenPlugin)
                            } else media
                        }
                    }))
            val mediaList:List[Media] = Await.result(future, Duration.apply(timeout, "second"))
	        println("mediaList :::: "+mediaList)
            if(null != mediaList && !mediaList.isEmpty)
                mediaList
            else medias
        } else medias
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
