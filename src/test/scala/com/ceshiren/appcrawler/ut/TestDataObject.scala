package com.ceshiren.appcrawler.ut

import com.ceshiren.appcrawler.core.CrawlerConf
import com.ceshiren.appcrawler.model.{Step, URIElement}
import com.ceshiren.appcrawler.utils.{TData}
import org.scalatest.{FunSuite, Matchers}

import scala.collection.mutable.ListBuffer
import scala.io.Source
import scala.reflect.io.File
import com.ceshiren.appcrawler.utils.Log.log
/**
  * Created by seveniruby on 16/8/13.
  */
class TestDataObject extends FunSuite  with Matchers{
  test("save to yaml file"){
    val a="中国"
    val yaml=TData.toYaml(a)
    val aa=TData.fromYaml[String](yaml)
    log.info(aa)
  }

  test("read clickedElementsList"){
    val yaml=scala.io.Source.fromFile("iOS_20160813165030/clickedList.yml").getLines().mkString("\n")
    val elementList=TData.fromYaml[List[URIElement]](yaml)
    log.info(elementList.head)
    log.info(elementList.last)

  }

  test("json to yaml"){
    val conf=new CrawlerConf().load("src/test/scala/com/xueqiu/qa/appcrawler/it/xueqiu_private.yml")
    log.info(conf)
    val yaml=TData.toYaml(conf)
    File("src/test/scala/com/xueqiu/qa/appcrawler/it/xueqiu_private.yml").writeAll(yaml)


  }

  test("convert json to yaml"){
    val file="src/universal/conf/xueqiu.json"
    val conf=new CrawlerConf().load(file)
    log.info(conf)
    val yaml=TData.toYaml(conf)
    File("src/universal/conf/xueqiu.yml").writeAll(yaml)
  }

  test("read json"){
    val conf=TData.fromJson[CrawlerConf](Source.fromFile("src/test/scala/com/xueqiu/qa/appcrawler/it/xueqiu_private.yml").getLines().mkString("\n"))
    log.info(conf.screenshot)
  }
  test("map yaml"){
    val url1=URIElement("a", "b", "c", "d", "e")
    val url2=URIElement("a", "b", "c", "d", "e")

    val u1=Map(2->url1)
    val u2=Map(url2->2)
    u1 should be equals(u2)

    val d1=TData.toYaml(u1)
    log.info(d1)

    val d2=TData.fromYaml[Map[String,Map[String, String]]](d1)
    val u22=URIElement(d2.get("2").get("url"), d2.get("2").get("tag"),
      d2.get("2").get("id"), d2.get("2").get("name"), d2.get("2").get("loc"))
    assert(u22.xpath==u1(2).xpath)

  }

  test("from html "){

    val xml=
      """
        |
        |<?xml version="1.0" encoding="UTF-8"?>
        |<hierarchy rotation="0">
        |  <android.widget.FrameLayout bounds="[0,0][768,1184]" checkable="false"
        |    checked="false" class="android.widget.FrameLayout" clickable="false"
        |    content-desc="" enabled="true" focusable="false" focused="false"
        |    index="0" instance="0" long-clickable="false"
        |    package="com.xueqiu.android" password="false" resource-id=""
        |    scrollable="false" selected="false" text="">
        |    <android.view.View bounds="[0,0][768,1184]" checkable="false"
        |      checked="false" class="android.view.View" clickable="false"
        |      content-desc="" enabled="true" focusable="false" focused="false"
        |      index="0" instance="0" long-clickable="false"
        |      package="com.xueqiu.android" password="false"
        |      resource-id="android:id/action_bar_overlay_layout"
        |      scrollable="false" selected="false" text="">
        |      <android.widget.FrameLayout bounds="[0,50][768,1184]"
        |        checkable="false" checked="false"
        |        class="android.widget.FrameLayout" clickable="false"
        |        content-desc="" enabled="true" focusable="false" focused="false"
        |        index="0" instance="1" long-clickable="false"
        |        package="com.xueqiu.android" password="false"
        |        resource-id="android:id/content" scrollable="false"
        |        selected="false" text="">
        |        <android.widget.RelativeLayout bounds="[0,50][768,1184]"
        |          checkable="false" checked="false"
        |          class="android.widget.RelativeLayout" clickable="false"
        |          content-desc="" enabled="true" focusable="false"
        |          focused="false" index="0" instance="0" long-clickable="false"
        |          package="com.xueqiu.android" password="false" resource-id=""
        |          scrollable="false" selected="false" text="">
        |          <android.widget.LinearLayout bounds="[0,50][768,1184]"
        |            checkable="false" checked="false"
        |            class="android.widget.LinearLayout" clickable="false"
        |            content-desc="" enabled="true" focusable="false"
        |            focused="false" index="0" instance="0"
        |            long-clickable="false" package="com.xueqiu.android"
        |            password="false"
        |            resource-id="com.xueqiu.android:id/ll_content"
        |            scrollable="false" selected="false" text="">
        |            <android.widget.RelativeLayout bounds="[0,50][768,1184]"
        |              checkable="false" checked="false"
        |              class="android.widget.RelativeLayout" clickable="false"
        |              content-desc="" enabled="true" focusable="false"
        |              focused="false" index="0" instance="1"
        |              long-clickable="false" package="com.xueqiu.android"
        |              password="false" resource-id="" scrollable="false"
        |              selected="false" text="">
        |              <android.widget.RelativeLayout bounds="[0,50][768,138]"
        |                checkable="false" checked="false"
        |                class="android.widget.RelativeLayout" clickable="false"
        |                content-desc="" enabled="true" focusable="false"
        |                focused="false" index="0" instance="2"
        |                long-clickable="false" package="com.xueqiu.android"
        |                password="false"
        |                resource-id="com.xueqiu.android:id/rl_head"
        |                scrollable="false" selected="false" text="">
        |                <android.widget.TextView bounds="[24,75][80,113]"
        |                  checkable="false" checked="false"
        |                  class="android.widget.TextView" clickable="true"
        |                  content-desc="" enabled="true" focusable="false"
        |                  focused="false" index="0" instance="0"
        |                  long-clickable="false" package="com.xueqiu.android"
        |                  password="false"
        |                  resource-id="com.xueqiu.android:id/tv_login"
        |                  scrollable="false" selected="false" text="登录"/>
        |                <android.widget.TextView bounds="[222,69][546,118]"
        |                  checkable="false" checked="false"
        |                  class="android.widget.TextView" clickable="false"
        |                  content-desc="" enabled="true" focusable="false"
        |                  focused="false" index="1" instance="1"
        |                  long-clickable="false" package="com.xueqiu.android"
        |                  password="false" resource-id="" scrollable="false"
        |                  selected="false" text="选择你感兴趣的话题"/>
        |                <android.widget.TextView bounds="[688,75][744,113]"
        |                  checkable="false" checked="false"
        |                  class="android.widget.TextView" clickable="true"
        |                  content-desc="" enabled="true" focusable="false"
        |                  focused="false" index="2" instance="2"
        |                  long-clickable="false" package="com.xueqiu.android"
        |                  password="false"
        |                  resource-id="com.xueqiu.android:id/tv_skip"
        |                  scrollable="false" selected="false" text="跳过"/>
        |                <android.view.View bounds="[0,137][768,138]"
        |                  checkable="false" checked="false"
        |                  class="android.view.View" clickable="false"
        |                  content-desc="" enabled="true" focusable="false"
        |                  focused="false" index="3" instance="1"
        |                  long-clickable="false" package="com.xueqiu.android"
        |                  password="false" resource-id="" scrollable="false"
        |                  selected="false" text=""/>
        |              </android.widget.RelativeLayout>
        |              <android.widget.LinearLayout bounds="[0,138][768,1184]"
        |                checkable="false" checked="false"
        |                class="android.widget.LinearLayout" clickable="false"
        |                content-desc="" enabled="true" focusable="false"
        |                focused="false" index="1" instance="1"
        |                long-clickable="false" package="com.xueqiu.android"
        |                password="false" resource-id="" scrollable="false"
        |                selected="false" text="">
        |                <android.view.View bounds="[64,163][704,1184]"
        |                  checkable="false" checked="false"
        |                  class="android.view.View" clickable="false"
        |                  content-desc="" enabled="true" focusable="false"
        |                  focused="false" index="0" instance="2"
        |                  long-clickable="false" package="com.xueqiu.android"
        |                  password="false"
        |                  resource-id="com.xueqiu.android:id/fl_topics"
        |                  scrollable="false" selected="false" text="">
        |                  <android.widget.CheckBox bounds="[212,178][556,265]"
        |                    checkable="true" checked="false"
        |                    class="android.widget.CheckBox" clickable="true"
        |                    content-desc="" enabled="true" focusable="true"
        |                    focused="false" index="0" instance="0"
        |                    long-clickable="false" package="com.xueqiu.android"
        |                    password="false" resource-id="" scrollable="false"
        |                    selected="false" text="滚雪球小帮手"/>
        |                  <android.widget.CheckBox bounds="[89,295][369,382]"
        |                    checkable="true" checked="false"
        |                    class="android.widget.CheckBox" clickable="true"
        |                    content-desc="" enabled="true" focusable="true"
        |                    focused="false" index="1" instance="1"
        |                    long-clickable="false" package="com.xueqiu.android"
        |                    password="false" resource-id="" scrollable="false"
        |                    selected="false" text="牛人牛股"/>
        |
        |                </android.view.View>
        |              </android.widget.LinearLayout>
        |              <android.view.View bounds="[0,1081][768,1082]"
        |                checkable="false" checked="false"
        |                class="android.view.View" clickable="false"
        |                content-desc="" enabled="true" focusable="false"
        |                focused="false" index="2" instance="3"
        |                long-clickable="false" package="com.xueqiu.android"
        |                password="false" resource-id="" scrollable="false"
        |                selected="false" text=""/>
        |              <android.widget.ImageView bounds="[46,758][721,1082]"
        |                checkable="false" checked="false"
        |                class="android.widget.ImageView" clickable="false"
        |                content-desc="" enabled="true" focusable="false"
        |                focused="false" index="3" instance="0"
        |                long-clickable="false" package="com.xueqiu.android"
        |                password="false"
        |                resource-id="com.xueqiu.android:id/iv_illustration"
        |                scrollable="false" selected="false" text=""/>
        |              <android.widget.TextView bounds="[0,1082][768,1184]"
        |                checkable="false" checked="false"
        |                class="android.widget.TextView" clickable="true"
        |                content-desc="" enabled="true" focusable="false"
        |                focused="false" index="4" instance="3"
        |                long-clickable="false" package="com.xueqiu.android"
        |                password="false"
        |                resource-id="com.xueqiu.android:id/tv_next_step"
        |                scrollable="false" selected="false" text="下一步"/>
        |            </android.widget.RelativeLayout>
        |          </android.widget.LinearLayout>
        |          <android.widget.LinearLayout bounds="[0,50][768,1184]"
        |            checkable="false" checked="false"
        |            class="android.widget.LinearLayout" clickable="false"
        |            content-desc="" enabled="true" focusable="false"
        |            focused="false" index="1" instance="2"
        |            long-clickable="false" package="com.xueqiu.android"
        |            password="false" resource-id="com.xueqiu.android:id/ll_back"
        |            scrollable="false" selected="false" text=""/>
        |        </android.widget.RelativeLayout>
        |      </android.widget.FrameLayout>
        |    </android.view.View>
        |  </android.widget.FrameLayout>
        |</hierarchy>
        |
        |
      """.stripMargin
    val m=TData.fromXML(xml)
    val fm=TData.flatten(m)

    println(m)
    println(fm)
    fm.foreach(println)
  }

  test("parse xml simple"){
    val xml=
      """
        |<hierarchy rotation="0">
        |  <android.widget.FrameLayout bounds="[0,0][768,1184]" checkable="false"
        |    checked="false" class="android.widget.FrameLayout" clickable="false"
        |    content-desc="" enabled="true" focusable="false" focused="false"
        |    index="0" instance="0" long-clickable="false"
        |    package="com.xueqiu.android" password="false" resource-id=""
        |    scrollable="false" selected="false" text="">
        |    <android.view.View bounds="[0,0][768,1184]" checkable="false"
        |      checked="false" class="android.view.View" clickable="false"
        |      content-desc="" enabled="true" focusable="false" focused="false"
        |      index="0" instance="0" long-clickable="false"
        |      package="com.xueqiu.android" password="false"
        |      resource-id="android:id/action_bar_overlay_layout"
        |      scrollable="false" selected="false" text="">
        |      <android.widget.FrameLayout bounds="[0,50][768,1184]"
        |        checkable="false" checked="false"
        |        class="android.widget.FrameLayout" clickable="false"
        |        content-desc="" enabled="true" focusable="false" focused="false"
        |        index="0" instance="1" long-clickable="false"
        |        package="com.xueqiu.android" password="false"
        |        resource-id="android:id/content" scrollable="false"
        |        selected="false" text="" />
        |   </android.view.View>
        |</android.widget.FrameLayout>
        |
      """.stripMargin

    val m=TData.fromXML(xml)
    val fm=TData.flatten(m)

    println(m)
    println(fm)
    println(fm.toSeq.sortBy(_._1))
    fm.foreach(println)
    fm.toSeq.sortBy(_._1).foreach(println)

  }
  test("from json"){
    val str=
      """
        |
        |{"status":0,"value":"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<AppiumAUT>\n    <XCUIElementTypeApplication name=\"雪球\" label=\"雪球\" value=\"\" dom=\"\" enabled=\"true\" valid=\"true\" visible=\"true\" hint=\"\" path=\"/0\" x=\"0\" y=\"0\" width=\"375\" height=\"667\">\n</XCUIElementTypeApplication>\n</AppiumAUT>","sessionId":"6a137283-8ceb-4df4-8a48-bf9d5de32659"}
      """.stripMargin
    log.info(TData.fromJson[Map[String, String]](str).getOrElse("value", ""))
  }

  test("load from yaml"){
    val conf=new CrawlerConf()
    conf.selectedList=ListBuffer[Step]()
    File("/tmp/1.yaml").writeAll(TData.toYaml(conf))

    val conf2=TData.fromYaml[CrawlerConf](Source.fromFile("/tmp/1.yaml").mkString)
    log.info(conf2.selectedList)

    conf.selectedList=null
    File("/tmp/2.yaml").writeAll(TData.toYaml(conf))

    val conf3=TData.fromYaml[CrawlerConf](Source.fromFile("/tmp/2.yaml").mkString)
    log.info(conf3.selectedList)

    val conf4=TData.fromYaml[CrawlerConf](Source.fromFile("/tmp/3.yaml").mkString)
    log.info(conf4.selectedList)




  }
}
