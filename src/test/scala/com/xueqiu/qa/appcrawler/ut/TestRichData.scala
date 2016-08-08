package com.xueqiu.qa.appcrawler.ut

import com.xueqiu.qa.appcrawler.{CommonLog, RichData, UrlElement}
import org.scalatest.{FunSuite, Matchers}

/**
  * Created by seveniruby on 16/3/26.
  */
class TestRichData extends FunSuite with Matchers with CommonLog{


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
      |        <android.widget.LinearLayout bounds="[0,50][768,1184]"
      |          checkable="false" checked="false"
      |          class="android.widget.LinearLayout" clickable="false"
      |          content-desc="" enabled="true" focusable="false"
      |          focused="false" index="0" instance="0" long-clickable="false"
      |          package="com.xueqiu.android" password="false" resource-id=""
      |          scrollable="false" selected="false" text="">
      |          <android.view.View bounds="[0,1082][768,1083]"
      |            checkable="false" checked="false" class="android.view.View"
      |            clickable="false" content-desc="" enabled="true"
      |            focusable="false" focused="false" index="1" instance="8"
      |            long-clickable="false" package="com.xueqiu.android"
      |            password="false" resource-id="com.xueqiu.android:id/line"
      |            scrollable="false" selected="false" text=""/>
      |          <android.widget.TabHost bounds="[0,1083][768,1184]"
      |            checkable="false" checked="false"
      |            class="android.widget.TabHost" clickable="false"
      |            content-desc="" enabled="true" focusable="true"
      |            focused="true" index="2" instance="0" long-clickable="false"
      |            package="com.xueqiu.android" password="false"
      |            resource-id="android:id/tabhost" scrollable="false"
      |            selected="false" text="">
      |            <android.widget.LinearLayout bounds="[0,1083][768,1184]"
      |              checkable="false" checked="false"
      |              class="android.widget.LinearLayout" clickable="false"
      |              content-desc="" enabled="true" focusable="false"
      |              focused="false" index="0" instance="13"
      |              long-clickable="false" package="com.xueqiu.android"
      |              password="false" resource-id="" scrollable="false"
      |              selected="false" text="">
      |              <android.widget.TabWidget bounds="[0,1083][768,1184]"
      |                checkable="false" checked="false"
      |                class="android.widget.TabWidget" clickable="false"
      |                content-desc="" enabled="true" focusable="true"
      |                focused="false" index="0" instance="0"
      |                long-clickable="false" package="com.xueqiu.android"
      |                password="false" resource-id="android:id/tabs"
      |                scrollable="false" selected="false" text="">
      |                <android.widget.RelativeLayout
      |                  bounds="[0,1083][153,1184]" checkable="false"
      |                  checked="false" class="android.widget.RelativeLayout"
      |                  clickable="true" content-desc="" enabled="true"
      |                  focusable="true" focused="false" index="0"
      |                  instance="1" long-clickable="false"
      |                  package="com.xueqiu.android" password="false"
      |                  resource-id="" scrollable="false" selected="false" text="">
      |                  <android.widget.ImageView bounds="[52,1093][100,1141]"
      |                    checkable="false" checked="false"
      |                    class="android.widget.ImageView" clickable="false"
      |                    content-desc="" enabled="true" focusable="false"
      |                    focused="false" index="0" instance="2"
      |                    long-clickable="false" package="com.xueqiu.android"
      |                    password="false"
      |                    resource-id="com.xueqiu.android:id/tab_icon"
      |                    scrollable="false" selected="false" text=""/>
      |                  <android.widget.TextView bounds="[40,1141][112,1174]"
      |                    checkable="false" checked="false"
      |                    class="android.widget.TextView" clickable="false"
      |                    content-desc="" enabled="true" focusable="false"
      |                    focused="false" index="1" instance="17"
      |                    long-clickable="false" package="com.xueqiu.android"
      |                    password="false"
      |                    resource-id="com.xueqiu.android:id/tab_name"
      |                    scrollable="false" selected="false" text="买什么"/>
      |                </android.widget.RelativeLayout>
      |                <android.widget.RelativeLayout
      |                  bounds="[153,1083][306,1184]" checkable="false"
      |                  checked="false" class="android.widget.RelativeLayout"
      |                  clickable="true" content-desc="" enabled="true"
      |                  focusable="true" focused="false" index="1"
      |                  instance="2" long-clickable="true"
      |                  package="com.xueqiu.android" password="false"
      |                  resource-id="" scrollable="false" selected="true" text="">
      |                  <android.widget.ImageView
      |                    bounds="[205,1093][253,1141]" checkable="false"
      |                    checked="false" class="android.widget.ImageView"
      |                    clickable="false" content-desc="" enabled="true"
      |                    focusable="false" focused="false" index="0"
      |                    instance="3" long-clickable="false"
      |                    package="com.xueqiu.android" password="false"
      |                    resource-id="com.xueqiu.android:id/tab_icon"
      |                    scrollable="false" selected="true" text=""/>
      |                  <android.widget.TextView bounds="[205,1141][253,1174]"
      |                    checkable="false" checked="false"
      |                    class="android.widget.TextView" clickable="false"
      |                    content-desc="" enabled="true" focusable="false"
      |                    focused="false" index="1" instance="18"
      |                    long-clickable="false" package="com.xueqiu.android"
      |                    password="false"
      |                    resource-id="com.xueqiu.android:id/tab_name"
      |                    scrollable="false" selected="true" text="自选"/>
      |                </android.widget.RelativeLayout>
      |                <android.widget.RelativeLayout
      |                  bounds="[306,1083][460,1184]" checkable="false"
      |                  checked="false" class="android.widget.RelativeLayout"
      |                  clickable="true" content-desc="" enabled="true"
      |                  focusable="true" focused="false" index="2"
      |                  instance="3" long-clickable="true"
      |                  package="com.xueqiu.android" password="false"
      |                  resource-id="" scrollable="false" selected="false" text="">
      |                  <android.widget.ImageView
      |                    bounds="[359,1093][407,1141]" checkable="false"
      |                    checked="false" class="android.widget.ImageView"
      |                    clickable="false" content-desc="" enabled="true"
      |                    focusable="false" focused="false" index="0"
      |                    instance="4" long-clickable="false"
      |                    package="com.xueqiu.android" password="false"
      |                    resource-id="com.xueqiu.android:id/tab_icon"
      |                    scrollable="false" selected="false" text=""/>
      |                  <android.widget.TextView bounds="[359,1141][407,1174]"
      |                    checkable="false" checked="false"
      |                    class="android.widget.TextView" clickable="false"
      |                    content-desc="" enabled="true" focusable="false"
      |                    focused="false" index="1" instance="19"
      |                    long-clickable="false" package="com.xueqiu.android"
      |                    password="false"
      |                    resource-id="com.xueqiu.android:id/tab_name"
      |                    scrollable="false" selected="false" text="动态"/>
      |                </android.widget.RelativeLayout>
      |                <android.widget.RelativeLayout
      |                  bounds="[460,1083][614,1184]" checkable="false"
      |                  checked="false" class="android.widget.RelativeLayout"
      |                  clickable="true" content-desc="" enabled="true"
      |                  focusable="true" focused="false" index="3"
      |                  instance="4" long-clickable="false"
      |                  package="com.xueqiu.android" password="false"
      |                  resource-id="" scrollable="false" selected="false" text="">
      |                  <android.widget.ImageView
      |                    bounds="[513,1093][561,1141]" checkable="false"
      |                    checked="false" class="android.widget.ImageView"
      |                    clickable="false" content-desc="" enabled="true"
      |                    focusable="false" focused="false" index="0"
      |                    instance="5" long-clickable="false"
      |                    package="com.xueqiu.android" password="false"
      |                    resource-id="com.xueqiu.android:id/tab_icon"
      |                    scrollable="false" selected="false" text=""/>
      |                  <android.widget.ImageView
      |                    bounds="[546,1093][605,1124]" checkable="false"
      |                    checked="false" class="android.widget.ImageView"
      |                    clickable="false" content-desc="" enabled="true"
      |                    focusable="false" focused="false" index="1"
      |                    instance="6" long-clickable="false"
      |                    package="com.xueqiu.android" password="false"
      |                    resource-id="com.xueqiu.android:id/unread_count"
      |                    scrollable="false" selected="false" text=""/>
      |                  <android.widget.TextView bounds="[513,1141][561,1174]"
      |                    checkable="false" checked="false"
      |                    class="android.widget.TextView" clickable="false"
      |                    content-desc="" enabled="true" focusable="false"
      |                    focused="false" index="2" instance="20"
      |                    long-clickable="false" package="com.xueqiu.android"
      |                    password="false"
      |                    resource-id="com.xueqiu.android:id/tab_name"
      |                    scrollable="false" selected="false" text="消息"/>
      |                </android.widget.RelativeLayout>
      |                <android.widget.RelativeLayout
      |                  bounds="[614,1083][768,1184]" checkable="false"
      |                  checked="false" class="android.widget.RelativeLayout"
      |                  clickable="true" content-desc="" enabled="true"
      |                  focusable="true" focused="false" index="4"
      |                  instance="5" long-clickable="false"
      |                  package="com.xueqiu.android" password="false"
      |                  resource-id="" scrollable="false" selected="false" text="">
      |                  <android.widget.ImageView
      |                    bounds="[667,1093][715,1141]" checkable="false"
      |                    checked="false" class="android.widget.ImageView"
      |                    clickable="false" content-desc="" enabled="true"
      |                    focusable="false" focused="false" index="0"
      |                    instance="7" long-clickable="false"
      |                    package="com.xueqiu.android" password="false"
      |                    resource-id="com.xueqiu.android:id/tab_icon"
      |                    scrollable="false" selected="false" text=""/>
      |                  <android.widget.TextView bounds="[667,1141][715,1174]"
      |                    checkable="false" checked="false"
      |                    class="android.widget.TextView" clickable="false"
      |                    content-desc="" enabled="true" focusable="false"
      |                    focused="false" index="1" instance="21"
      |                    long-clickable="false" package="com.xueqiu.android"
      |                    password="false"
      |                    resource-id="com.xueqiu.android:id/tab_name"
      |                    scrollable="false" selected="false" text="交易"/>
      |                </android.widget.RelativeLayout>
      |              </android.widget.TabWidget>
      |            </android.widget.LinearLayout>
      |          </android.widget.TabHost>
      |        </android.widget.LinearLayout>
      |      </android.widget.FrameLayout>
      |      <android.widget.FrameLayout bounds="[0,50][768,146]"
      |        checkable="false" checked="false"
      |        class="android.widget.FrameLayout" clickable="false"
      |        content-desc="" enabled="true" focusable="false" focused="false"
      |        index="1" instance="12" long-clickable="false"
      |        package="com.xueqiu.android" password="false"
      |        resource-id="android:id/action_bar_container" scrollable="false"
      |        selected="false" text="">
      |        <android.view.View bounds="[0,50][768,146]" checkable="false"
      |          checked="false" class="android.view.View" clickable="false"
      |          content-desc="" enabled="true" focusable="false"
      |          focused="false" index="0" instance="9" long-clickable="false"
      |          package="com.xueqiu.android" password="false"
      |          resource-id="android:id/action_bar" scrollable="false"
      |          selected="false" text="">
      |          <android.widget.HorizontalScrollView bounds="[0,50][304,146]"
      |            checkable="false" checked="false"
      |            class="android.widget.HorizontalScrollView"
      |            clickable="false" content-desc="" enabled="true"
      |            focusable="true" focused="false" index="0" instance="0"
      |            long-clickable="false" package="com.xueqiu.android"
      |            password="false" resource-id="" scrollable="false"
      |            selected="false" text="">
      |            <android.widget.LinearLayout bounds="[0,50][304,146]"
      |              checkable="false" checked="false"
      |              class="android.widget.LinearLayout" clickable="false"
      |              content-desc="" enabled="true" focusable="false"
      |              focused="false" index="0" instance="14"
      |              long-clickable="false" package="com.xueqiu.android"
      |              password="false" resource-id="" scrollable="false"
      |              selected="false" text="">
      |              <android.widget.TextView bounds="[0,50][152,146]"
      |                checkable="false" checked="false"
      |                class="android.widget.TextView" clickable="true"
      |                content-desc="" enabled="true" focusable="true"
      |                focused="false" index="0" instance="22"
      |                long-clickable="false" package="com.xueqiu.android"
      |                password="false" resource-id="" scrollable="false"
      |                selected="false" text="组合"/>
      |              <android.widget.TextView bounds="[152,50][304,146]"
      |                checkable="false" checked="false"
      |                class="android.widget.TextView" clickable="true"
      |                content-desc="" enabled="true" focusable="true"
      |                focused="false" index="1" instance="23"
      |                long-clickable="false" package="com.xueqiu.android"
      |                password="false" resource-id="" scrollable="false"
      |                selected="true" text="股票"/>
      |            </android.widget.LinearLayout>
      |          </android.widget.HorizontalScrollView>
      |          <android.widget.LinearLayout bounds="[544,50][768,146]"
      |            checkable="false" checked="false"
      |            class="android.widget.LinearLayout" clickable="false"
      |            content-desc="" enabled="true" focusable="false"
      |            focused="false" index="2" instance="15"
      |            long-clickable="false" package="com.xueqiu.android"
      |            password="false" resource-id="" scrollable="false"
      |            selected="false" text="">
      |            <android.widget.TextView bounds="[544,50][656,146]"
      |              checkable="false" checked="false"
      |              class="android.widget.TextView" clickable="true"
      |              content-desc="编辑" enabled="true" focusable="true"
      |              focused="false" index="0" instance="24"
      |              long-clickable="true" package="com.xueqiu.android"
      |              password="false"
      |              resource-id="com.xueqiu.android:id/action_edit"
      |              scrollable="false" selected="false" text=""/>
      |            <android.widget.TextView bounds="[656,50][768,146]"
      |              checkable="false" checked="false"
      |              class="android.widget.TextView" clickable="true"
      |              content-desc="输入股票名称/代码" enabled="true" focusable="true"
      |              focused="false" index="1" instance="25"
      |              long-clickable="true" package="com.xueqiu.android"
      |              password="false"
      |              resource-id="com.xueqiu.android:id/action_search"
      |              scrollable="false" selected="false" text=""/>
      |          </android.widget.LinearLayout>
      |        </android.view.View>
      |      </android.widget.FrameLayout>
      |    </android.view.View>
      |  </android.widget.FrameLayout>
      |</hierarchy>
      |
    """.stripMargin

  val dom=RichData.toXML(xml)


  test("parse xpath"){
    val node=RichData.getListFromXPath("//*[@resource-id='com.xueqiu.android:id/action_search']", dom)(0)
    println(node)
    node("resource-id") should be equals("com.xueqiu.android:id/action_search")
    node("content-desc") should be equals("输入股票名称/代码")
  }

  test("getPackage"){
    val node=RichData.getListFromXPath("(//*[@package!=''])[1]", dom)(0)
    println(node)
  }
  test("extra attribute from xpath"){
    val node=RichData.getListFromXPath("//*[@resource-id='com.xueqiu.android:id/action_search']/@resource-id", dom)(0)
    println(node)
    node.values.toList(0) should be equals("com.xueqiu.android:id/action_search")

    //todo:暂不支持
    val value=RichData.getListFromXPath("string(//*[@resource-id='com.xueqiu.android:id/action_search']/@resource-id)", dom)
    println(value)

  }

  test("get parent path"){
    val value=RichData.getListFromXPath("//*[@resource-id='com.xueqiu.android:id/action_search']/parent::*", dom)
    value.foreach(println)
    println(value)

    val ancestor=RichData.getListFromXPath("//*[@resource-id='com.xueqiu.android:id/action_search']/ancestor-or-self::*", dom)
    ancestor.foreach(x=>if(x.contains("tag")) println(x("tag")))
    println(ancestor)
    ancestor.foreach(println)

    val ancestorName=RichData.getListFromXPath("//*[@resource-id='com.xueqiu.android:id/action_search']/ancestor::name", dom)
    ancestorName.foreach(println)
  }

  test("xpath parse"){
    val xml=
      """
        |<?xml version="1.0" encoding="UTF-8"?>
        |<AppiumAUT>
        |  <UIAApplication dom="" enabled="true" height="1004" hint="" label="雪球"
        |    name="雪球" path="/0" valid="true" value="" visible="true" width="768"
        |    x="0" y="20">
        |    <UIAWindow dom="" enabled="true" height="1024" hint="" label=""
        |      name="" path="/0/0" valid="true" value="" visible="true"
        |      width="768" x="0" y="0">
        |      <UIANavigationBar dom="" enabled="true" height="44" hint=""
        |        label="" name="SNBCubeDetailVC" path="/0/0/0" valid="true"
        |        value="" visible="true" width="768" x="0" y="20">
        |        <UIAImage dom="" enabled="true" height="64" hint="" label=""
        |          name="" path="/0/0/0/0" valid="true" value="" visible="false"
        |          width="768" x="0" y="0">
        |          <UIAImage dom="" enabled="true" height="0" hint="" label=""
        |            name="" path="/0/0/0/0/0" valid="true" value=""
        |            visible="false" width="768" x="0" y="64"/>
        |        </UIAImage>
        |        <UIAButton dom="" enabled="true" height="21" hint="" label="返回"
        |          name="返回" path="/0/0/0/1" valid="true" value=""
        |          visible="false" width="21" x="12" y="31.5"/>
        |        <UIAButton dom="" enabled="true" height="32" hint=""
        |          label="nav_icon_back" name="nav_icon_back" path="/0/0/0/2"
        |          valid="true" value="" visible="true" width="30" x="20" y="26"/>
        |        <UIAStaticText dom="" enabled="true" height="16" hint=""
        |          label="平衡组合" name="平衡组合" path="/0/0/0/3" valid="true"
        |          value="平衡组合" visible="true" width="64" x="352" y="26"/>
        |        <UIAStaticText dom="" enabled="true" height="14.5" hint=""
        |          label="ZH124423" name="ZH124423" path="/0/0/0/4" valid="true"
        |          value="ZH124423" visible="true" width="58" x="355" y="45"/>
        |        <UIAStaticText dom="" enabled="true" height="12" hint=""
        |          label="" name="" path="/0/0/0/5" valid="true" value=""
        |          visible="false" width="160" x="304" y="64"/>
        |        <UIAButton dom="" enabled="true" height="24" hint="" label="关注"
        |          name="关注" path="/0/0/0/6" valid="true" value="" visible="true"
        |          width="60" x="697" y="30"/>
        |      </UIANavigationBar>
        |      <UIATableView dom="" enabled="true" height="984" hint="" label=""
        |        name="" path="/0/0/1" valid="true" value="第 1 航至第 7 航（共 10 航）"
        |        visible="true" width="768" x="0" y="0">
        |        <UIATableCell dom="" enabled="true" height="96" hint="" label=""
        |          name="JamesLv" path="/0/0/1/0" valid="true" value=""
        |          visible="true" width="768" x="0" y="244">
        |          <UIAStaticText dom="" enabled="true" height="29" hint=""
        |            label="JamesLv" name="JamesLv" path="/0/0/1/0/0"
        |            valid="true" value="JamesLv" visible="true" width="50"
        |            x="60" y="251"/>
        |          <UIAStaticText dom="" enabled="true" height="38" hint=""
        |            label="其实是个高换手组合，多数都是隔日交易，偏向于价值投机，专注于医药和消费类个股，偶尔投机概念股。 仓位控制+专注个股是核心！非实盘，请不要据此组合操作！！"
        |            name="其实是个高换手组合，多数都是隔日交易，偏向于价值投机，专注于医药和消费类个股，偶尔投机概念股。 仓位控制+专注个股是核心！非实盘，请不要据此组合操作！！"
        |            path="/0/0/1/0/1" valid="true"
        |            value="其实是个高换手组合，多数都是隔日交易，偏向于价值投机，专注于医药和消费类个股，偶尔投机概念股。 仓位控制+专注个股是核心！非实盘，请不要据此组合操作！！"
        |            visible="true" width="696" x="60" y="280"/>
        |          <UIAStaticText dom="" enabled="true" height="14.5" hint=""
        |            label="主理" name="主理" path="/0/0/1/0/2" valid="true"
        |            value="主理" visible="true" width="24" x="113" y="258.5"/>
        |        </UIATableCell>
        |        <UIATableCell dom="" enabled="true" height="35" hint="" label=""
        |          name="业绩评级 (最后评估时间：2016-04-12)" path="/0/0/1/1" valid="true"
        |          value="" visible="true" width="768" x="0" y="340">
        |          <UIAStaticText dom="" enabled="true" height="17" hint=""
        |            label="业绩评级 (最后评估时间：2016-04-12)"
        |            name="业绩评级 (最后评估时间：2016-04-12)" path="/0/0/1/1/0"
        |            valid="true" value="业绩评级 (最后评估时间：2016-04-12)" visible="true"
        |            width="733" x="12" y="349"/>
        |        </UIATableCell>
        |        <UIATableCell dom="" enabled="true" height="260" hint=""
        |          label="" name="No.1" path="/0/0/1/2" valid="true" value=""
        |          visible="true" width="768" x="0" y="375">
        |          <UIAStaticText dom="" enabled="true" height="12" hint=""
        |            label="No.1" name="No.1" path="/0/0/1/2/0" valid="true"
        |            value="No.1" visible="true" width="38" x="9" y="417.5"/>
        |          <UIAStaticText dom="" enabled="true"
        |            height="26.688000000000045" hint="" label="持股分散度"
        |            name="持股分散度" path="/0/0/1/2/1" valid="true" value="持股分散度"
        |            visible="true" width="100" x="260.4344018096886" y="506.34955481705987"/>
        |          <UIAStaticText dom="" enabled="true"
        |            height="26.687999999999988" hint="" label="可复制性" name="可复制性"
        |            path="/0/0/1/2/2" valid="true" value="可复制性" visible="true"
        |            width="86" x="269.9521767166335" y="454.59319160137693"/>
        |          <UIAStaticText dom="" enabled="true" height="60" hint=""
        |            label="77" name="77" path="/0/0/1/2/3" valid="true"
        |            value="77" visible="true" width="60" x="354" y="449"/>
        |          <UIAStaticText dom="" enabled="true"
        |            height="26.687999999999988" hint="" label="盈利能力" name="盈利能力"
        |            path="/0/0/1/2/4" valid="true" value="盈利能力" visible="true"
        |            width="86" x="378" y="377.656"/>
        |          <UIAStaticText dom="" enabled="true"
        |            height="26.687999999999988" hint="" label="稳定性" name="稳定性"
        |            path="/0/0/1/2/5" valid="true" value="稳定性" visible="true"
        |            width="72" x="421.2022160434968" y="525.1187490865586"/>
        |          <UIAStaticText dom="" enabled="true"
        |            height="26.687999999999988" hint="" label="抗风险能力"
        |            name="抗风险能力" path="/0/0/1/2/6" valid="true" value="抗风险能力"
        |            visible="true" width="100" x="456.1768456394616" y="440.2548030623793"/>
        |          <UIAButton dom="" enabled="true" height="32" hint=""
        |            label="icon cube medal" name="icon cube medal"
        |            path="/0/0/1/2/7" valid="true" value="" visible="true"
        |            width="32" x="12" y="387"/>
        |          <UIAButton dom="" enabled="true" height="24" hint=""
        |            label="查看雪球星级评价标准" name="查看雪球星级评价标准" path="/0/0/1/2/8"
        |            valid="true" value="" visible="true" width="100" x="334" y="611"/>
        |        </UIATableCell>
        |        <UIATableGroup dom="" enabled="true" height="10" hint=""
        |          label="" name="" path="/0/0/1/3" valid="true" value=""
        |          visible="false" width="768" x="0" y="635"/>
        |        <UIATableCell dom="" enabled="true" height="35" hint="" label=""
        |          name="最新调仓 (2016-04-06 13:47:21)" path="/0/0/1/4" valid="true"
        |          value="" visible="true" width="768" x="0" y="645">
        |          <UIAStaticText dom="" enabled="true" height="17" hint=""
        |            label="最新调仓 (2016-04-06 13:47:21)"
        |            name="最新调仓 (2016-04-06 13:47:21)" path="/0/0/1/4/0"
        |            valid="true" value="最新调仓 (2016-04-06 13:47:21)"
        |            visible="true" width="677" x="12" y="654"/>
        |          <UIAStaticText dom="" enabled="true" height="17" hint=""
        |            label="调仓分析" name="调仓分析" path="/0/0/1/4/1" valid="true"
        |            value="调仓分析" visible="true" width="56" x="689" y="654"/>
        |        </UIATableCell>
        |        <UIATableCell dom="" enabled="true" height="50" hint="" label=""
        |          name="买" path="/0/0/1/5" valid="true" value="" visible="true"
        |          width="768" x="0" y="680">
        |          <UIAStaticText dom="" enabled="true" height="16" hint=""
        |            label="买" name="买" path="/0/0/1/5/0" valid="true" value="买"
        |            visible="true" width="16" x="12" y="697"/>
        |          <UIAStaticText dom="" enabled="true" height="17.5" hint=""
        |            label="人福医药" name="人福医药" path="/0/0/1/5/1" valid="true"
        |            value="人福医药" visible="true" width="521" x="33" y="690.5"/>
        |          <UIAStaticText dom="" enabled="true" height="12" hint=""
        |            label="SH600079" name="SH600079" path="/0/0/1/5/2"
        |            valid="true" value="SH600079" visible="true" width="47.5"
        |            x="33" y="708"/>
        |          <UIAStaticText dom="" enabled="true" height="19" hint=""
        |            label="29.59% → 40.00%" name="29.59% → 40.00%"
        |            path="/0/0/1/5/3" valid="true" value="29.59% → 40.00%"
        |            visible="true" width="137" x="559" y="689.5"/>
        |          <UIAStaticText dom="" enabled="true" height="12" hint=""
        |            label="参考成交价 17.67" name="参考成交价 17.67" path="/0/0/1/5/4"
        |            valid="true" value="参考成交价 17.67" visible="true" width="78"
        |            x="618" y="708"/>
        |          <UIAButton dom="" enabled="true" height="32" hint=""
        |            label="下单" name="下单" path="/0/0/1/5/5" valid="true" value=""
        |            visible="true" width="48" x="708" y="689"/>
        |        </UIATableCell>
        |        <UIATableGroup dom="" enabled="true" height="10" hint=""
        |          label="" name="" path="/0/0/1/6" valid="true" value=""
        |          visible="false" width="768" x="0" y="730"/>
        |        <UIATableCell dom="" enabled="true" height="35" hint="" label=""
        |          name="股票配置" path="/0/0/1/7" valid="true" value=""
        |          visible="true" width="768" x="0" y="740">
        |          <UIAStaticText dom="" enabled="true" height="17" hint=""
        |            label="股票配置" name="股票配置" path="/0/0/1/7/0" valid="true"
        |            value="股票配置" visible="true" width="677" x="12" y="749"/>
        |          <UIAStaticText dom="" enabled="true" height="17" hint=""
        |            label="详细仓位" name="详细仓位" path="/0/0/1/7/1" valid="true"
        |            value="详细仓位" visible="true" width="56" x="689" y="749"/>
        |        </UIATableCell>
        |        <UIATableCell dom="" enabled="true" height="272" hint=""
        |          label="" name="最近三个月调仓 19 次， 7 次赚了钱" path="/0/0/1/8"
        |          valid="true" value="" visible="true" width="768" x="0" y="775">
        |          <UIAStaticText dom="" enabled="true" height="18" hint=""
        |            label="最近三个月调仓 19 次， 7 次赚了钱" name="最近三个月调仓 19 次， 7 次赚了钱"
        |            path="/0/0/1/8/0" valid="true" value="最近三个月调仓 19 次， 7 次赚了钱"
        |            visible="false" width="189.5" x="289.5" y="995"/>
        |          <UIAButton dom="" enabled="true" height="25" hint=""
        |            label="持仓股票收益分析" name="持仓股票收益分析" path="/0/0/1/8/1"
        |            valid="true" value="" visible="false" width="145" x="311.5" y="1012"/>
        |        </UIATableCell>
        |        <UIATableGroup dom="" enabled="true" height="10" hint=""
        |          label="" name="" path="/0/0/1/9" valid="true" value=""
        |          visible="false" width="768" x="0" y="974"/>
        |        <UIATableCell dom="" enabled="true" height="35" hint="" label=""
        |          name="收益率走势 (创建于2015-01-07)" path="/0/0/1/10" valid="true"
        |          value="" visible="false" width="768" x="0" y="1057"/>
        |        <UIATableCell dom="" enabled="true" height="250" hint=""
        |          label="" name="沪深300" path="/0/0/1/11" valid="true" value=""
        |          visible="false" width="768" x="0" y="1092">
        |          <UIAStaticText dom="" enabled="true" height="12" hint=""
        |            label="沪深300" name="沪深300" path="/0/0/1/11/0" valid="true"
        |            value="沪深300" visible="false" width="37" x="0" y="0"/>
        |          <UIAStaticText dom="" enabled="true" height="12" hint=""
        |            label="组合" name="组合" path="/0/0/1/11/1" valid="true"
        |            value="组合" visible="false" width="20" x="0" y="0"/>
        |          <UIAStaticText dom="" enabled="true" height="14" hint=""
        |            label="!" name="!" path="/0/0/1/11/2" valid="true" value="!"
        |            visible="false" width="14" x="0" y="0"/>
        |          <UIAStaticText dom="" enabled="true" height="12" hint=""
        |            label="提示： 组合数据与实际操作结果存在差异" name="提示： 组合数据与实际操作结果存在差异"
        |            path="/0/0/1/11/3" valid="true" value="提示： 组合数据与实际操作结果存在差异"
        |            visible="false" width="183" x="0" y="0"/>
        |          <UIAStaticText dom="" enabled="true" height="11" hint=""
        |            label="-10.00%" name="-10.00%" path="/0/0/1/11/4"
        |            valid="true" value="-10.00%" visible="false" width="45"
        |            x="0" y="0"/>
        |          <UIAStaticText dom="" enabled="true" height="11" hint=""
        |            label="-5.00%" name="-5.00%" path="/0/0/1/11/5" valid="true"
        |            value="-5.00%" visible="false" width="45" x="0" y="0"/>
        |          <UIAStaticText dom="" enabled="true" height="11" hint=""
        |            label="0.00%" name="0.00%" path="/0/0/1/11/6" valid="true"
        |            value="0.00%" visible="false" width="45" x="0" y="0"/>
        |          <UIAStaticText dom="" enabled="true" height="11" hint=""
        |            label="5.00%" name="5.00%" path="/0/0/1/11/7" valid="true"
        |            value="5.00%" visible="false" width="45" x="0" y="0"/>
        |          <UIAStaticText dom="" enabled="true" height="10" hint=""
        |            label="2016-04-05" name="2016-04-05" path="/0/0/1/11/8"
        |            valid="true" value="2016-04-05" visible="false" width="60"
        |            x="0" y="0"/>
        |          <UIAStaticText dom="" enabled="true" height="10" hint=""
        |            label="2016-03-22" name="2016-03-22" path="/0/0/1/11/9"
        |            valid="true" value="2016-03-22" visible="false" width="60"
        |            x="0" y="0"/>
        |          <UIAStaticText dom="" enabled="true" height="10" hint=""
        |            label="2016-03-09" name="2016-03-09" path="/0/0/1/11/10"
        |            valid="true" value="2016-03-09" visible="false" width="60"
        |            x="0" y="0"/>
        |          <UIAStaticText dom="" enabled="true" height="10" hint=""
        |            label="2016-02-25" name="2016-02-25" path="/0/0/1/11/11"
        |            valid="true" value="2016-02-25" visible="false" width="60"
        |            x="0" y="0"/>
        |          <UIAStaticText dom="" enabled="true" height="10" hint=""
        |            label="2016-02-05" name="2016-02-05" path="/0/0/1/11/12"
        |            valid="true" value="2016-02-05" visible="false" width="60"
        |            x="0" y="0"/>
        |          <UIAStaticText dom="" enabled="true" height="10" hint=""
        |            label="2016-01-25" name="2016-01-25" path="/0/0/1/11/13"
        |            valid="true" value="2016-01-25" visible="false" width="60"
        |            x="0" y="0"/>
        |          <UIAButton dom="" enabled="true" height="30" hint=""
        |            label="最近三个月" name="最近三个月" path="/0/0/1/11/14" valid="true"
        |            value="" visible="false" width="74" x="0" y="0"/>
        |          <UIAButton dom="" enabled="true" height="30" hint=""
        |            label="最近一年" name="最近一年" path="/0/0/1/11/15" valid="true"
        |            value="" visible="false" width="74" x="0" y="0"/>
        |        </UIATableCell>
        |        <UIATableGroup dom="" enabled="true" height="44" hint=""
        |          label="" name="" path="/0/0/1/12" valid="true" value=""
        |          visible="false" width="768" x="0" y="1342">
        |          <UIAButton dom="" enabled="true" height="39" hint=""
        |            label="主理人说(1)" name="主理人说(1)" path="/0/0/1/12/0"
        |            valid="true" value="" visible="false" width="384" x="0" y="0"/>
        |          <UIAButton dom="" enabled="true" height="39" hint=""
        |            label="所有点评(69)" name="所有点评(69)" path="/0/0/1/12/1"
        |            valid="true" value="" visible="false" width="384" x="0" y="0"/>
        |        </UIATableGroup>
        |        <UIATableCell dom="" enabled="true" height="128" hint=""
        |          label="" name=" " path="/0/0/1/13" valid="true" value=""
        |          visible="false" width="768" x="0" y="1386">
        |          <UIAStaticText dom="" enabled="true" height="128" hint=""
        |            label=" " name=" " path="/0/0/1/13/0" valid="true" value=""
        |            visible="false" width="768" x="0" y="1386"/>
        |        </UIATableCell>
        |      </UIATableView>
        |      <UIAButton dom="" enabled="true" height="44" hint="" label="下单"
        |        name="下单" path="/0/0/2" valid="true" value="" visible="true"
        |        width="256" x="0" y="980"/>
        |      <UIAButton dom="" enabled="true" height="44" hint="" label="点评"
        |        name="点评" path="/0/0/3" valid="true" value="" visible="true"
        |        width="256" x="256" y="980"/>
        |      <UIAButton dom="" enabled="true" height="44" hint="" label="分享"
        |        name="分享" path="/0/0/4" valid="true" value="" visible="true"
        |        width="256" x="512" y="980"/>
        |      <UIATabBar dom="" enabled="true" height="49" hint="" label=""
        |        name="" path="/0/0/5" valid="true" value="" visible="false"
        |        width="768" x="0" y="975">
        |        <UIAImage dom="" enabled="true" height="0.5" hint="" label=""
        |          name="" path="/0/0/5/0" valid="true" value="" visible="false"
        |          width="768" x="0" y="974.5"/>
        |        <UIAImage dom="" enabled="true" height="49" hint="" label=""
        |          name="" path="/0/0/5/1" valid="true" value="" visible="false"
        |          width="768" x="0" y="975"/>
        |        <UIAButton dom="" enabled="true" height="48" hint="" label="首页"
        |          name="首页" path="/0/0/5/2" valid="true" value="1"
        |          visible="false" width="76" x="126" y="976"/>
        |        <UIAButton dom="" enabled="true" height="48" hint="" label="自选"
        |          name="自选" path="/0/0/5/3" valid="true" value=""
        |          visible="false" width="76" x="236" y="976"/>
        |        <UIAButton dom="" enabled="true" height="48" hint="" label="动态"
        |          name="动态" path="/0/0/5/4" valid="true" value="56 项"
        |          visible="false" width="76" x="346" y="976"/>
        |        <UIAButton dom="" enabled="true" height="48" hint="" label="组合"
        |          name="组合" path="/0/0/5/5" valid="true" value=""
        |          visible="false" width="76" x="456" y="976"/>
        |        <UIAButton dom="" enabled="true" height="48" hint="" label="交易"
        |          name="交易" path="/0/0/5/6" valid="true" value=""
        |          visible="false" width="76" x="566" y="976"/>
        |      </UIATabBar>
        |      <UIAImage dom="" enabled="true" height="75" hint="" label=""
        |        name="" path="/0/0/6" valid="true" value="" visible="true"
        |        width="208" x="6" y="905"/>
        |      <UIAButton dom="" enabled="true" height="75" hint="" label="好"
        |        name="好" path="/0/0/7" valid="true" value="" visible="true"
        |        width="44" x="166" y="905"/>
        |      <UIAStaticText dom="" enabled="true" height="34" hint=""
        |        label="这里可以批量实盘买卖组合持仓股票" name="这里可以批量实盘买卖组合持仓股票" path="/0/0/8"
        |        valid="true" value="这里可以批量实盘买卖组合持仓股票" visible="true" width="141"
        |        x="25" y="921"/>
        |    </UIAWindow>
        |    <UIAWindow dom="" enabled="true" height="1024" hint="" label=""
        |      name="" path="/0/1" valid="true" value="" visible="false"
        |      width="768" x="0" y="0"/>
        |    <UIAWindow dom="" enabled="true" height="1024" hint="" label=""
        |      name="" path="/0/2" valid="true" value="" visible="true"
        |      width="768" x="0" y="0">
        |      <UIAStatusBar dom="" enabled="true" height="20" hint="" label=""
        |        name="" path="/0/2/0" valid="true" value="" visible="true"
        |        width="768" x="0" y="0">
        |        <UIAElement dom="" enabled="true" height="20"
        |          hint="用三个手指向下轻扫来显现通知中心。, 用三个手指向上轻扫来显现控制中心, 连按两次来滚动到顶部"
        |          label="iPad" name="iPad" path="/0/2/0/0" valid="true" value=""
        |          visible="true" width="25" x="6" y="0"/>
        |        <UIAElement dom="" enabled="true" height="20"
        |          hint="用三个手指向下轻扫来显现通知中心。, 用三个手指向上轻扫来显现控制中心, 连按两次来滚动到顶部"
        |          label="3 格无线局域网信号（共 3 格）" name="3 格无线局域网信号（共 3 格）"
        |          path="/0/2/0/1" valid="true" value="XueQiu" visible="true"
        |          width="13" x="36" y="0"/>
        |        <UIAElement dom="" enabled="true" height="20"
        |          hint="用三个手指向下轻扫来显现通知中心。, 用三个手指向上轻扫来显现控制中心, 连按两次来滚动到顶部"
        |          label="下午12:13" name="下午12:13" path="/0/2/0/2" valid="true"
        |          value="" visible="true" width="52" x="358" y="0"/>
        |        <UIAElement dom="" enabled="true" height="20"
        |          hint="用三个手指向下轻扫来显现通知中心。, 用三个手指向上轻扫来显现控制中心, 连按两次来滚动到顶部"
        |          label="不在充电" name="不在充电" path="/0/2/0/3" valid="true" value=""
        |          visible="true" width="48" x="684" y="0"/>
        |        <UIAElement dom="" enabled="true" height="20"
        |          hint="用三个手指向下轻扫来显现通知中心。, 用三个手指向上轻扫来显现控制中心, 连按两次来滚动到顶部"
        |          label="电池电量：55%, 正在充电" name="电池电量：55%, 正在充电" path="/0/2/0/4"
        |          valid="true" value="" visible="true" width="25" x="738" y="0"/>
        |      </UIAStatusBar>
        |    </UIAWindow>
        |  </UIAApplication>
        |</AppiumAUT>
        |
      """.stripMargin


    val dom=RichData.toXML(xml)
    val value=RichData.getListFromXPath("//UIAApplication[@name=\"雪球\" and @path=\"/0\"]/UIAWindow[@path=\"/0/0\"]/UIAStaticText[@name=\"这里可以批量实盘买卖组合持仓股票\" and @path=\"/0/0/8\"]", dom)
    value.foreach(println)
    println(value)

    val appName=RichData.getListFromXPath("//UIAApplication", dom)
    appName.foreach(println)
    println(appName.head.getOrElse("name", ""))
    appName.head.getOrElse("name", "") should be equals("雪球")



  }

  test("android to tag path"){
    val str=
      """
        |//android.widget.FrameLayout[@index="0"]/android.view.View[@index="0" and @resource-id="android:id/decor_content_parent"]/android.widget.FrameLayout[@index="0" and @resource-id="android:id/content"]/android.widget.LinearLayout[@index="0"]/android.widget.TabHost[@index="2" and @resource-id="android:id/tabhost"]/android.widget.LinearLayout[@index="0"]/android.widget.TabWidget[@index="0" and @resource-id="android:id/tabs"]/android.widget.RelativeLayout[@index="0"]/android.widget.TextView[@index="1" and @resource-id="com.xueqiu.android:id/tab_name"]
        |
      """.stripMargin

    val ele=UrlElement("", "", "", "", str)
    println(ele.toTagPath())
    println(ele.toFileName())

  }
  test("ios to tag path"){
    val str=
      """
        |//UIAApplication[@name="雪球" and @path="/0"]/UIAWindow[@path="/0/0"]/UIATableView[@path="/0/0/1"]/UIATableCell[@name="恒瑞医药业绩会，高管说了啥？" and @path="/0/0/1/10"]/UIAStaticText[@name="恒瑞医药业绩会，高管说了啥？" and @path="/0/0/1/10/0"]
      """.stripMargin
    val ele=UrlElement("", "", "", "", str)
    log.info(ele.toTagPath())
    log.info(ele.toFileName())

  }

  test("get all leaf node"){
    val value=RichData.getListFromXPath("//node()[not(node())]", dom)
    value.foreach(log.info)
  }


  test("text xpath"){
    val value=RichData.getListFromXPath("//*[@text='买什么']", dom)
    value.foreach(log.info)
  }
}
