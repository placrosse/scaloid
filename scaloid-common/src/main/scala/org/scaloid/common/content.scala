/* 
 *
 * 
 *
 *
 * Less painful Android development with Scala
 *
 * http://scaloid.org
 *
 *
 *
 *
 *
 *
 * Copyright 2013 Sung-Ho Lee
 *
 * Sung-Ho Lee licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

/*
 * This file is automatically generated. Any changes on this file will be overwritten!
 */

package org.scaloid.common

import android.content._
import android.util.Log
import android.os._
import scala.collection.mutable.ArrayBuffer


class EventSource0[T] extends ArrayBuffer[() => T] {
  def apply(e: => T) = append(() => e)

  def run() {
    foreach(_())
  }
}

class EventSource1[T <: Function1[_, _]] extends ArrayBuffer[T] {
  def apply(e: T) = append(e)
}

class EventSource2[T <: Function2[_, _, _]] extends ArrayBuffer[T] {
  def apply(e: T) = append(e)
}

trait Destroyable {
  protected val onDestroyBodies = new ArrayBuffer[() => Any]

  def onDestroy(body: => Any) = {
    val el = (() => body)
    onDestroyBodies += el
    el
  }
}

trait Creatable {
  protected val onCreateBodies = new ArrayBuffer[() => Any]

  def onCreate(body: => Any) = {
    val el = (() => body)
    onCreateBodies += el
    el
  }
}

trait Registerable {
  def onRegister(body: => Any): () => Any
  def onUnregister(body: => Any): () => Any
}


class RichContext[V <: android.content.Context](val basis: V) extends TraitContext[V]

trait TraitContext[V <: android.content.Context] extends TagUtil {

  def basis: V

  implicit val ctx = basis

  def startActivity[T: ClassManifest] {
    basis.startActivity(SIntent[T])
  }

  def startService[T: ClassManifest] {
    basis.startService(SIntent[T])
  }

  def stopService[T: ClassManifest] {
    basis.stopService(SIntent[T])
  }

  @inline def applicationContext = basis.getApplicationContext

  @inline def applicationInfo = basis.getApplicationInfo

  @inline def assets = basis.getAssets

  @inline def cacheDir = basis.getCacheDir

  @inline def classLoader = basis.getClassLoader

  @inline def contentResolver = basis.getContentResolver

  @inline def externalCacheDir = basis.getExternalCacheDir

  @inline def filesDir = basis.getFilesDir

  @inline def mainLooper = basis.getMainLooper

  @inline def packageCodePath = basis.getPackageCodePath

  @inline def packageManager = basis.getPackageManager

  @inline def packageName = basis.getPackageName

  @inline def packageResourcePath = basis.getPackageResourcePath

  @inline def resources = basis.getResources

  @inline def theme = basis.getTheme
  @inline def theme  (p: Int) =            theme_=  (p)
  @inline def theme_=(p: Int) = { basis.setTheme    (p); basis }

  @inline def wallpaper = basis.getWallpaper
  @inline def wallpaper  (p: android.graphics.Bitmap) =            wallpaper_=  (p)
  @inline def wallpaper_=(p: android.graphics.Bitmap) = { basis.setWallpaper    (p); basis }
  @inline def wallpaper  (p: java.io.InputStream) =            wallpaper_=  (p)
  @inline def wallpaper_=(p: java.io.InputStream) = { basis.setWallpaper    (p); basis }

  @inline def wallpaperDesiredMinimumHeight = basis.getWallpaperDesiredMinimumHeight

  @inline def wallpaperDesiredMinimumWidth = basis.getWallpaperDesiredMinimumWidth


}


class RichContextWrapper[V <: android.content.ContextWrapper](val basis: V) extends TraitContextWrapper[V]

trait TraitContextWrapper[V <: android.content.ContextWrapper] extends TraitContext[V] {




  @inline def baseContext = basis.getBaseContext


}

class SContextWrapper()(implicit base: android.content.Context)
    extends android.content.ContextWrapper(base) with TraitContextWrapper[SContextWrapper] {

  val basis = this


}

object SContextWrapper {
  def apply()(implicit base: android.content.Context): SContextWrapper = {
    val v = new SContextWrapper
    v
  }

}




trait UnregisterReceiver extends ContextWrapper with Destroyable {
  override def registerReceiver(receiver: BroadcastReceiver, filter: IntentFilter): android.content.Intent = {
    onDestroy {
      Log.i("ScalaUtils", "Unregister BroadcastReceiver: "+receiver)
      try {
        unregisterReceiver(receiver)
      } catch {
        // Suppress "Receiver not registered" exception
        // Refer to http://stackoverflow.com/questions/2682043/how-to-check-if-receiver-is-registered-in-android
        case e: IllegalArgumentException => e.printStackTrace()
      }
    }

    super.registerReceiver(receiver, filter)
  }
}


object SIntent {
  @inline def apply[T](implicit context: Context, mt: ClassManifest[T]) = new Intent(context, mt.erasure)

  @inline def apply[T](action: String)(implicit context: Context, mt: ClassManifest[T]): Intent = SIntent[T].setAction(action)
}


class LocalServiceConnection[S <: LocalService](bindFlag: Int = Context.BIND_AUTO_CREATE)(implicit ctx: Context, reg: Registerable, ev: Null <:< S, mf: ClassManifest[S]) extends ServiceConnection {
  var service: S = null
  var componentName:ComponentName = _
  var binder: IBinder = _
  var onConnected = new EventSource0[Unit]
  var onDisconnected = new EventSource0[Unit]

  def onServiceConnected(p1: ComponentName, b: IBinder) {
    service = (b.asInstanceOf[LocalService#ScaloidServiceBinder]).service.asInstanceOf[S]
    componentName = p1
    binder = b
    onConnected.run()
  }

  def onServiceDisconnected(p1: ComponentName) {
    service = null
    onDisconnected.run()
  }

  def connected: Boolean = service != null

  reg.onRegister {
    ctx.bindService(SIntent[S], this, bindFlag)
  }

  reg.onUnregister {
    ctx.unbindService(this)
  }
}
