package com.testerhome.appcrawler.ut

import com.testerhome.appcrawler._
import org.apache.commons.text.StringEscapeUtils
import org.scalatest.{FunSuite, Matchers}
import org.w3c.dom.NodeList

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.io.Source
import collection.JavaConverters._

/**
  * Created by seveniruby on 16/3/26.
  */
class TestXPathUtil extends FunSuite with Matchers with CommonLog{


  val xmlAndroid=
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

  val domAndroid=XPathUtil.toDocument(xmlAndroid)


  val xmlIOS=
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


  val domIOS=XPathUtil.toDocument(xmlIOS)


  test("parse xpath"){
    log.info(XPathUtil.getNodeListByXPath("//*[@resource-id='com.xueqiu.android:id/action_search']", domAndroid))
    val node=XPathUtil.getNodeListByXPath("//*[@resource-id='com.xueqiu.android:id/action_search']", domAndroid)(0)
    println(node)
    node("resource-id") should be equals("com.xueqiu.android:id/action_search")
    node("content-desc") should be equals("输入股票名称/代码")
  }

  test("getPackage"){
    val node=XPathUtil.getNodeListByXPath("(//*[@package!=''])[1]", domAndroid)(0)
    println(node)
  }
  test("extra attribute from xpath"){
    val node=XPathUtil.getNodeListByXPath("//*[@resource-id='com.xueqiu.android:id/action_search']/@resource-id", domAndroid)(0)
    println(node)
    node.values.toList(0) should be equals("com.xueqiu.android:id/action_search")

    //todo:暂不支持
    val value=XPathUtil.getNodeListByXPath("string(//*[@resource-id='com.xueqiu.android:id/action_search']/@resource-id)", domAndroid)
    println(value)

  }

  test("get parent path"){
    val value=XPathUtil.getNodeListByXPath("//*[@resource-id='com.xueqiu.android:id/action_search']/parent::*", domAndroid)
    value.foreach(println)
    println(value)

    val ancestor=XPathUtil.getNodeListByXPath("//*[@resource-id='com.xueqiu.android:id/action_search']/ancestor-or-self::*", domAndroid)
    ancestor.foreach(x=>if(x.contains("tag")) println(x("tag")))
    println(ancestor)
    ancestor.foreach(println)

    val ancestorName=XPathUtil.getNodeListByXPath("//*[@resource-id='com.xueqiu.android:id/action_search']/ancestor::name", domAndroid)
    ancestorName.foreach(println)
  }

  test("xpath parse"){

    val value=XPathUtil.getNodeListByXPath("//UIAApplication[@name=\"雪球\" and @path=\"/0\"]/UIAWindow[@path=\"/0/0\"]/UIAStaticText[@name=\"这里可以批量实盘买卖组合持仓股票\" and @path=\"/0/0/8\"]", domIOS)
    value.foreach(println)
    println(value)

    val appName=XPathUtil.getNodeListByXPath("//UIAApplication", domIOS)
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

    val ele=URIElement("", "", "", "", str)
    println(ele.getAncestor())
    println(ele.toString())

  }
  test("ios to tag path"){
    val str=
      """
        |//UIAApplication[@name="雪球" and @path="/0"]/UIAWindow[@path="/0/0"]/UIATableView[@path="/0/0/1"]/UIATableCell[@name="恒瑞医药业绩会，高管说了啥？" and @path="/0/0/1/10"]/UIAStaticText[@name="恒瑞医药业绩会，高管说了啥？" and @path="/0/0/1/10/0"]
      """.stripMargin
    val ele=URIElement("", "", "", "", str)
    log.info(ele.getAncestor())
    log.info(ele.toString())

  }

  test("get all leaf node"){
    val value=XPathUtil.getNodeListByXPath("//node()[not(node())]", domAndroid)
    value.foreach(log.info)
  }


  //todo: 支持xpath2.0
  test("text xpath matches"){
    val value=XPathUtil.getNodeListByXPath("//*[matches(@text, '买什么')]", domAndroid)
    value.foreach(log.info)
  }

  test("xpath and"){
    val content=
      """
        |<?xml version="1.0" encoding="UTF-8"?>
        |<hierarchy rotation="0">
        |  <android.widget.FrameLayout bounds="[0,0][1080,1812]"
        |    checkable="false" checked="false" class="android.widget.FrameLayout"
        |    clickable="false" content-desc="" enabled="true" focusable="false"
        |    focused="false" index="0" instance="0" long-clickable="false"
        |    package="com.tencent.mobileqq" password="false" resource-id=""
        |    scrollable="false" selected="false" text="">
        |    <android.widget.RelativeLayout bounds="[0,0][1080,1812]"
        |      checkable="false" checked="false"
        |      class="android.widget.RelativeLayout" clickable="false"
        |      content-desc="" enabled="true" focusable="false" focused="false"
        |      index="0" instance="0" long-clickable="false"
        |      package="com.tencent.mobileqq" password="false" resource-id=""
        |      scrollable="false" selected="false" text="">
        |      <android.widget.LinearLayout bounds="[0,0][1080,1812]"
        |        checkable="false" checked="false"
        |        class="android.widget.LinearLayout" clickable="false"
        |        content-desc="" enabled="true" focusable="false" focused="false"
        |        index="0" instance="0" long-clickable="false"
        |        package="com.tencent.mobileqq" password="false" resource-id=""
        |        scrollable="false" selected="false" text="">
        |        <android.widget.FrameLayout bounds="[0,0][1080,1812]"
        |          checkable="false" checked="false"
        |          class="android.widget.FrameLayout" clickable="false"
        |          content-desc="" enabled="true" focusable="false"
        |          focused="false" index="1" instance="1" long-clickable="false"
        |          package="com.tencent.mobileqq" password="false"
        |          resource-id="android:id/content" scrollable="false"
        |          selected="false" text="">
        |          <android.widget.FrameLayout bounds="[0,0][1080,1812]"
        |            checkable="false" checked="false"
        |            class="android.widget.FrameLayout" clickable="false"
        |            content-desc="" enabled="true" focusable="false"
        |            focused="false" index="0" instance="2"
        |            long-clickable="false" package="com.tencent.mobileqq"
        |            password="false" resource-id="com.tencent.mobileqq:id/name"
        |            scrollable="false" selected="false" text="">
        |            <android.widget.FrameLayout bounds="[0,0][1080,1812]"
        |              checkable="false" checked="false"
        |              class="android.widget.FrameLayout" clickable="false"
        |              content-desc="" enabled="true" focusable="true"
        |              focused="false" index="0" instance="3"
        |              long-clickable="false" package="com.tencent.mobileqq"
        |              password="false"
        |              resource-id="com.tencent.mobileqq:id/common_xlistview"
        |              scrollable="true" selected="false" text="">
        |              <android.widget.LinearLayout bounds="[0,0][1080,1812]"
        |                checkable="false" checked="false"
        |                class="android.widget.LinearLayout" clickable="false"
        |                content-desc="" enabled="true" focusable="false"
        |                focused="false" index="0" instance="1"
        |                long-clickable="false" package="com.tencent.mobileqq"
        |                password="false"
        |                resource-id="com.tencent.mobileqq:id/name"
        |                scrollable="false" selected="false" text="">
        |                <android.widget.RelativeLayout bounds="[0,0][1080,1181]"
        |                  checkable="false" checked="false"
        |                  class="android.widget.RelativeLayout"
        |                  clickable="false"
        |                  content-desc="帐号信息昵称通灵郎男  乌鲁木齐2013-04-24"
        |                  enabled="true" focusable="false" focused="false"
        |                  index="0" instance="1" long-clickable="false"
        |                  package="com.tencent.mobileqq" password="false"
        |                  resource-id="com.tencent.mobileqq:id/name"
        |                  scrollable="false" selected="false" text="">
        |                  <android.widget.LinearLayout
        |                    bounds="[0,636][1080,1181]" checkable="false"
        |                    checked="false" class="android.widget.LinearLayout"
        |                    clickable="false" content-desc="群成员资料"
        |                    enabled="true" focusable="false" focused="false"
        |                    index="0" instance="2" long-clickable="false"
        |                    package="com.tencent.mobileqq" password="false"
        |                    resource-id="" scrollable="false" selected="false" text="">
        |                    <android.widget.TextView bounds="[432,813][648,898]"
        |                      checkable="false" checked="false"
        |                      class="android.widget.TextView" clickable="true"
        |                      content-desc="群名称：通灵郎" enabled="true"
        |                      focusable="true" focused="false" index="0"
        |                      instance="0" long-clickable="true"
        |                      package="com.tencent.mobileqq" password="false"
        |                      resource-id="com.tencent.mobileqq:id/name"
        |                      scrollable="false" selected="false" text="通灵郎"/>
        |                    <android.widget.RelativeLayout
        |                      bounds="[485,922][595,975]" checkable="false"
        |                      checked="false"
        |                      class="android.widget.RelativeLayout"
        |                      clickable="true" content-desc="" enabled="true"
        |                      focusable="false" focused="false" index="1"
        |                      instance="2" long-clickable="false"
        |                      package="com.tencent.mobileqq" password="false"
        |                      resource-id="com.tencent.mobileqq:id/name"
        |                      scrollable="false" selected="false" text="">
        |                      <android.widget.TextView
        |                        bounds="[485,922][595,975]" checkable="false"
        |                        checked="false" class="android.widget.TextView"
        |                        clickable="false" content-desc="" enabled="true"
        |                        focusable="false" focused="false" index="0"
        |                        instance="1" long-clickable="false"
        |                        package="com.tencent.mobileqq" password="false"
        |                        resource-id="com.tencent.mobileqq:id/name"
        |                        scrollable="false" selected="false" text=" 学渣 "/>
        |                    </android.widget.RelativeLayout>
        |                    <android.widget.LinearLayout
        |                      bounds="[54,1005][1026,1054]" checkable="false"
        |                      checked="false"
        |                      class="android.widget.LinearLayout"
        |                      clickable="false" content-desc="" enabled="true"
        |                      focusable="false" focused="false" index="2"
        |                      instance="3" long-clickable="false"
        |                      package="com.tencent.mobileqq" password="false"
        |                      resource-id="" scrollable="false" selected="false" text="">
        |                      <android.widget.TextView
        |                        bounds="[54,1005][1026,1054]" checkable="false"
        |                        checked="false" class="android.widget.TextView"
        |                        clickable="true"
        |                        content-desc="个性签名今天的苦果，是昨天的伏笔；当下的付出，才是明日的花开。"
        |                        enabled="true" focusable="false" focused="false"
        |                        index="0" instance="2" long-clickable="false"
        |                        package="com.tencent.mobileqq" password="false"
        |                        resource-id="com.tencent.mobileqq:id/name"
        |                        scrollable="false" selected="false" text="今天的苦果，是昨天的伏笔；当下的付出，才是明日的花开。"/>
        |                    </android.widget.LinearLayout>
        |                    <android.widget.LinearLayout
        |                      bounds="[418,1093][661,1139]" checkable="false"
        |                      checked="false"
        |                      class="android.widget.LinearLayout"
        |                      clickable="false" content-desc="" enabled="true"
        |                      focusable="false" focused="false" index="3"
        |                      instance="4" long-clickable="false"
        |                      package="com.tencent.mobileqq" password="false"
        |                      resource-id="com.tencent.mobileqq:id/name"
        |                      scrollable="false" selected="false" text="">
        |                      <android.widget.TextView
        |                        bounds="[460,1093][661,1139]" checkable="false"
        |                        checked="false" class="android.widget.TextView"
        |                        clickable="false" content-desc="" enabled="true"
        |                        focusable="false" focused="false" index="0"
        |                        instance="3" long-clickable="false"
        |                        package="com.tencent.mobileqq" password="false"
        |                        resource-id="com.tencent.mobileqq:id/name"
        |                        scrollable="false" selected="false" text="乌鲁木齐"/>
        |                    </android.widget.LinearLayout>
        |                  </android.widget.LinearLayout>
        |                  <android.widget.ImageView bounds="[388,486][691,789]"
        |                    checkable="false" checked="false"
        |                    class="android.widget.ImageView" clickable="false"
        |                    content-desc="" enabled="true" focusable="false"
        |                    focused="false" index="1" instance="0"
        |                    long-clickable="false"
        |                    package="com.tencent.mobileqq" password="false"
        |                    resource-id="com.tencent.mobileqq:id/name"
        |                    scrollable="false" selected="false" text=""/>
        |                  <android.widget.ImageView bounds="[390,486][690,786]"
        |                    checkable="false" checked="false"
        |                    class="android.widget.ImageView" clickable="true"
        |                    content-desc="查看大头像" enabled="true"
        |                    focusable="false" focused="false" index="2"
        |                    instance="1" long-clickable="false"
        |                    package="com.tencent.mobileqq" password="false"
        |                    resource-id="com.tencent.mobileqq:id/name"
        |                    scrollable="false" selected="false" text=""/>
        |                </android.widget.RelativeLayout>
        |                <android.widget.RelativeLayout
        |                  bounds="[0,1181][1080,1319]" checkable="false"
        |                  checked="false" class="android.widget.RelativeLayout"
        |                  clickable="true" content-desc="帐号信息549628569非会员"
        |                  enabled="true" focusable="false" focused="false"
        |                  index="1" instance="3" long-clickable="true"
        |                  package="com.tencent.mobileqq" password="false"
        |                  resource-id="" scrollable="false" selected="false" text="">
        |                  <android.widget.ImageView bounds="[36,1212][111,1287]"
        |                    checkable="false" checked="false"
        |                    class="android.widget.ImageView" clickable="false"
        |                    content-desc="" enabled="true" focusable="false"
        |                    focused="false" index="0" instance="2"
        |                    long-clickable="false"
        |                    package="com.tencent.mobileqq" password="false"
        |                    resource-id="com.tencent.mobileqq:id/name"
        |                    scrollable="false" selected="false" text=""/>
        |                  <android.widget.LinearLayout
        |                    bounds="[147,1183][1008,1317]" checkable="false"
        |                    checked="false" class="android.widget.LinearLayout"
        |                    clickable="false" content-desc="" enabled="true"
        |                    focusable="false" focused="false" index="1"
        |                    instance="5" long-clickable="false"
        |                    package="com.tencent.mobileqq" password="false"
        |                    resource-id="com.tencent.mobileqq:id/name"
        |                    scrollable="false" selected="false" text="">
        |                    <android.widget.LinearLayout
        |                      bounds="[147,1216][1008,1284]" checkable="false"
        |                      checked="false"
        |                      class="android.widget.LinearLayout"
        |                      clickable="false" content-desc="" enabled="true"
        |                      focusable="false" focused="false" index="0"
        |                      instance="6" long-clickable="false"
        |                      package="com.tencent.mobileqq" password="false"
        |                      resource-id="" scrollable="false" selected="false" text="">
        |                      <android.widget.TextView
        |                        bounds="[147,1216][408,1284]" checkable="false"
        |                        checked="false" class="android.widget.TextView"
        |                        clickable="false" content-desc="" enabled="true"
        |                        focusable="false" focused="false" index="0"
        |                        instance="4" long-clickable="false"
        |                        package="com.tencent.mobileqq" password="false"
        |                        resource-id="com.tencent.mobileqq:id/name"
        |                        scrollable="false" selected="false" text="549628569"/>
        |                    </android.widget.LinearLayout>
        |                  </android.widget.LinearLayout>
        |                  <android.widget.ImageView
        |                    bounds="[1008,1227][1035,1272]" checkable="false"
        |                    checked="false" class="android.widget.ImageView"
        |                    clickable="false" content-desc="" enabled="true"
        |                    focusable="false" focused="false" index="2"
        |                    instance="3" long-clickable="false"
        |                    package="com.tencent.mobileqq" password="false"
        |                    resource-id="com.tencent.mobileqq:id/name"
        |                    scrollable="false" selected="false" text=""/>
        |                </android.widget.RelativeLayout>
        |                <android.widget.RelativeLayout
        |                  bounds="[0,1319][1080,1457]" checkable="false"
        |                  checked="false" class="android.widget.RelativeLayout"
        |                  clickable="true" content-desc="QQ空间通灵郎的空间"
        |                  enabled="true" focusable="false" focused="false"
        |                  index="2" instance="4" long-clickable="false"
        |                  package="com.tencent.mobileqq" password="false"
        |                  resource-id="" scrollable="false" selected="false" text="">
        |                  <android.widget.ImageView bounds="[38,1350][113,1425]"
        |                    checkable="false" checked="false"
        |                    class="android.widget.ImageView" clickable="false"
        |                    content-desc="" enabled="true" focusable="false"
        |                    focused="false" index="0" instance="4"
        |                    long-clickable="false"
        |                    package="com.tencent.mobileqq" password="false"
        |                    resource-id="com.tencent.mobileqq:id/name"
        |                    scrollable="false" selected="false" text=""/>
        |                  <android.widget.LinearLayout
        |                    bounds="[149,1321][1008,1455]" checkable="false"
        |                    checked="false" class="android.widget.LinearLayout"
        |                    clickable="false" content-desc="" enabled="true"
        |                    focusable="false" focused="false" index="1"
        |                    instance="7" long-clickable="false"
        |                    package="com.tencent.mobileqq" password="false"
        |                    resource-id="com.tencent.mobileqq:id/name"
        |                    scrollable="false" selected="false" text="">
        |                    <android.widget.LinearLayout
        |                      bounds="[149,1354][1008,1422]" checkable="false"
        |                      checked="false"
        |                      class="android.widget.LinearLayout"
        |                      clickable="false" content-desc="" enabled="true"
        |                      focusable="false" focused="false" index="0"
        |                      instance="8" long-clickable="false"
        |                      package="com.tencent.mobileqq" password="false"
        |                      resource-id="" scrollable="false" selected="false" text="">
        |                      <android.widget.TextView
        |                        bounds="[149,1354][455,1422]" checkable="false"
        |                        checked="false" class="android.widget.TextView"
        |                        clickable="false" content-desc="" enabled="true"
        |                        focusable="false" focused="false" index="0"
        |                        instance="5" long-clickable="false"
        |                        package="com.tencent.mobileqq" password="false"
        |                        resource-id="com.tencent.mobileqq:id/name"
        |                        scrollable="false" selected="false" text="通灵郎的空间"/>
        |                    </android.widget.LinearLayout>
        |                  </android.widget.LinearLayout>
        |                  <android.widget.ImageView
        |                    bounds="[1008,1365][1035,1410]" checkable="false"
        |                    checked="false" class="android.widget.ImageView"
        |                    clickable="false" content-desc="" enabled="true"
        |                    focusable="false" focused="false" index="2"
        |                    instance="5" long-clickable="false"
        |                    package="com.tencent.mobileqq" password="false"
        |                    resource-id="com.tencent.mobileqq:id/name"
        |                    scrollable="false" selected="false" text=""/>
        |                </android.widget.RelativeLayout>
        |                <android.widget.TextView bounds="[0,1457][1080,1514]"
        |                  checkable="false" checked="false"
        |                  class="android.widget.TextView" clickable="false"
        |                  content-desc="" enabled="true" focusable="false"
        |                  focused="false" index="3" instance="6"
        |                  long-clickable="false" package="com.tencent.mobileqq"
        |                  password="false" resource-id="" scrollable="false"
        |                  selected="false" text=""/>
        |                <android.widget.RelativeLayout
        |                  bounds="[0,1514][1080,1652]" checkable="false"
        |                  checked="false" class="android.widget.RelativeLayout"
        |                  clickable="true" content-desc="群聊等级" enabled="true"
        |                  focusable="false" focused="false" index="4"
        |                  instance="5" long-clickable="false"
        |                  package="com.tencent.mobileqq" password="false"
        |                  resource-id="" scrollable="false" selected="false" text="">
        |                  <android.widget.ImageView bounds="[36,1545][111,1620]"
        |                    checkable="false" checked="false"
        |                    class="android.widget.ImageView" clickable="false"
        |                    content-desc="" enabled="true" focusable="false"
        |                    focused="false" index="0" instance="6"
        |                    long-clickable="false"
        |                    package="com.tencent.mobileqq" password="false"
        |                    resource-id="com.tencent.mobileqq:id/name"
        |                    scrollable="false" selected="false" text=""/>
        |                  <android.widget.LinearLayout
        |                    bounds="[147,1516][1008,1650]" checkable="false"
        |                    checked="false" class="android.widget.LinearLayout"
        |                    clickable="false" content-desc="" enabled="true"
        |                    focusable="false" focused="false" index="1"
        |                    instance="9" long-clickable="false"
        |                    package="com.tencent.mobileqq" password="false"
        |                    resource-id="com.tencent.mobileqq:id/name"
        |                    scrollable="false" selected="false" text="">
        |                    <android.widget.LinearLayout
        |                      bounds="[147,1549][1008,1617]" checkable="false"
        |                      checked="false"
        |                      class="android.widget.LinearLayout"
        |                      clickable="false" content-desc="" enabled="true"
        |                      focusable="false" focused="false" index="0"
        |                      instance="10" long-clickable="false"
        |                      package="com.tencent.mobileqq" password="false"
        |                      resource-id="" scrollable="false" selected="false" text="">
        |                      <android.widget.TextView
        |                        bounds="[147,1549][609,1617]" checkable="false"
        |                        checked="false" class="android.widget.TextView"
        |                        clickable="false" content-desc="" enabled="true"
        |                        focusable="false" focused="false" index="0"
        |                        instance="7" long-clickable="false"
        |                        package="com.tencent.mobileqq" password="false"
        |                        resource-id="com.tencent.mobileqq:id/name"
        |                        scrollable="false" selected="false" text="群聊等级已达LV1 "/>
        |                    </android.widget.LinearLayout>
        |                  </android.widget.LinearLayout>
        |                  <android.widget.ImageView
        |                    bounds="[1008,1560][1035,1605]" checkable="false"
        |                    checked="false" class="android.widget.ImageView"
        |                    clickable="false" content-desc="" enabled="true"
        |                    focusable="false" focused="false" index="2"
        |                    instance="7" long-clickable="false"
        |                    package="com.tencent.mobileqq" password="false"
        |                    resource-id="com.tencent.mobileqq:id/name"
        |                    scrollable="false" selected="false" text=""/>
        |                </android.widget.RelativeLayout>
        |                <android.widget.RelativeLayout
        |                  bounds="[0,1652][1080,1790]" checkable="false"
        |                  checked="false" class="android.widget.RelativeLayout"
        |                  clickable="true" content-desc="最近发言无本地发言记录"
        |                  enabled="true" focusable="false" focused="false"
        |                  index="5" instance="6" long-clickable="false"
        |                  package="com.tencent.mobileqq" password="false"
        |                  resource-id="" scrollable="false" selected="false" text="">
        |                  <android.widget.ImageView bounds="[38,1683][113,1758]"
        |                    checkable="false" checked="false"
        |                    class="android.widget.ImageView" clickable="false"
        |                    content-desc="" enabled="true" focusable="false"
        |                    focused="false" index="0" instance="8"
        |                    long-clickable="false"
        |                    package="com.tencent.mobileqq" password="false"
        |                    resource-id="com.tencent.mobileqq:id/name"
        |                    scrollable="false" selected="false" text=""/>
        |                  <android.widget.LinearLayout
        |                    bounds="[149,1654][1008,1788]" checkable="false"
        |                    checked="false" class="android.widget.LinearLayout"
        |                    clickable="false" content-desc="" enabled="true"
        |                    focusable="false" focused="false" index="1"
        |                    instance="11" long-clickable="false"
        |                    package="com.tencent.mobileqq" password="false"
        |                    resource-id="com.tencent.mobileqq:id/name"
        |                    scrollable="false" selected="false" text="">
        |                    <android.widget.LinearLayout
        |                      bounds="[149,1687][1008,1755]" checkable="false"
        |                      checked="false"
        |                      class="android.widget.LinearLayout"
        |                      clickable="false" content-desc="" enabled="true"
        |                      focusable="false" focused="false" index="0"
        |                      instance="12" long-clickable="false"
        |                      package="com.tencent.mobileqq" password="false"
        |                      resource-id="" scrollable="false" selected="false" text="">
        |                      <android.widget.TextView
        |                        bounds="[149,1687][506,1755]" checkable="false"
        |                        checked="false" class="android.widget.TextView"
        |                        clickable="false" content-desc="" enabled="true"
        |                        focusable="false" focused="false" index="0"
        |                        instance="8" long-clickable="false"
        |                        package="com.tencent.mobileqq" password="false"
        |                        resource-id="com.tencent.mobileqq:id/name"
        |                        scrollable="false" selected="false" text="无本地发言记录"/>
        |                    </android.widget.LinearLayout>
        |                  </android.widget.LinearLayout>
        |                  <android.widget.ImageView
        |                    bounds="[1008,1698][1035,1743]" checkable="false"
        |                    checked="false" class="android.widget.ImageView"
        |                    clickable="false" content-desc="" enabled="true"
        |                    focusable="false" focused="false" index="2"
        |                    instance="9" long-clickable="false"
        |                    package="com.tencent.mobileqq" password="false"
        |                    resource-id="com.tencent.mobileqq:id/name"
        |                    scrollable="false" selected="false" text=""/>
        |                </android.widget.RelativeLayout>
        |              </android.widget.LinearLayout>
        |            </android.widget.FrameLayout>
        |            <android.widget.ImageView bounds="[0,0][1080,636]"
        |              checkable="false" checked="false"
        |              class="android.widget.ImageView" clickable="false"
        |              content-desc="" enabled="true" focusable="false"
        |              focused="false" index="1" instance="10"
        |              long-clickable="false" package="com.tencent.mobileqq"
        |              password="false"
        |              resource-id="com.tencent.mobileqq:id/name"
        |              scrollable="false" selected="false" text=""/>
        |            <android.widget.RelativeLayout bounds="[0,0][1080,223]"
        |              checkable="false" checked="false"
        |              class="android.widget.RelativeLayout" clickable="false"
        |              content-desc="" enabled="true" focusable="false"
        |              focused="false" index="2" instance="7"
        |              long-clickable="false" package="com.tencent.mobileqq"
        |              password="false"
        |              resource-id="com.tencent.mobileqq:id/name"
        |              scrollable="false" selected="false" text="">
        |              <android.widget.RelativeLayout bounds="[0,73][1080,223]"
        |                checkable="false" checked="false"
        |                class="android.widget.RelativeLayout" clickable="false"
        |                content-desc="" enabled="true" focusable="false"
        |                focused="false" index="0" instance="8"
        |                long-clickable="false" package="com.tencent.mobileqq"
        |                password="false" resource-id="" scrollable="false"
        |                selected="false" text="">
        |                <android.widget.TextView bounds="[30,94][195,202]"
        |                  checkable="false" checked="false"
        |                  class="android.widget.TextView" clickable="true"
        |                  content-desc="返回" enabled="true" focusable="false"
        |                  focused="false" index="0" instance="9"
        |                  long-clickable="false" package="com.tencent.mobileqq"
        |                  password="false"
        |                  resource-id="com.tencent.mobileqq:id/ivTitleBtnLeft"
        |                  scrollable="false" selected="false" text="返回"/>
        |                <android.widget.TextView bounds="[928,103][1053,193]"
        |                  checkable="false" checked="false"
        |                  class="android.widget.TextView" clickable="true"
        |                  content-desc="更多" enabled="true" focusable="false"
        |                  focused="false" index="1" instance="10"
        |                  long-clickable="false" package="com.tencent.mobileqq"
        |                  password="false"
        |                  resource-id="com.tencent.mobileqq:id/ivTitleBtnRightText"
        |                  scrollable="false" selected="false" text="更多"/>
        |              </android.widget.RelativeLayout>
        |            </android.widget.RelativeLayout>
        |            <android.view.View bounds="[0,636][1080,1812]"
        |              checkable="false" checked="false"
        |              class="android.view.View" clickable="false"
        |              content-desc="" enabled="true" focusable="false"
        |              focused="false" index="3" instance="0"
        |              long-clickable="false" package="com.tencent.mobileqq"
        |              password="false" resource-id="" scrollable="false"
        |              selected="false" text=""/>
        |            <android.widget.LinearLayout bounds="[0,1644][1080,1812]"
        |              checkable="false" checked="false"
        |              class="android.widget.LinearLayout" clickable="false"
        |              content-desc="" enabled="true" focusable="false"
        |              focused="false" index="4" instance="13"
        |              long-clickable="false" package="com.tencent.mobileqq"
        |              password="false"
        |              resource-id="com.tencent.mobileqq:id/name"
        |              scrollable="false" selected="false" text="">
        |              <android.widget.Button bounds="[30,1662][350,1794]"
        |                checkable="false" checked="false"
        |                class="android.widget.Button" clickable="true"
        |                content-desc="加好友" enabled="true" focusable="true"
        |                focused="false" index="0" instance="0"
        |                long-clickable="false" package="com.tencent.mobileqq"
        |                password="false"
        |                resource-id="com.tencent.mobileqq:id/name"
        |                scrollable="false" selected="false" text="加好友"/>
        |              <android.widget.Button bounds="[380,1662][700,1794]"
        |                checkable="false" checked="false"
        |                class="android.widget.Button" clickable="true"
        |                content-desc="送礼物" enabled="true" focusable="true"
        |                focused="false" index="1" instance="1"
        |                long-clickable="false" package="com.tencent.mobileqq"
        |                password="false"
        |                resource-id="com.tencent.mobileqq:id/name"
        |                scrollable="false" selected="false" text="送礼物"/>
        |              <android.widget.Button bounds="[730,1662][1050,1794]"
        |                checkable="false" checked="false"
        |                class="android.widget.Button" clickable="true"
        |                content-desc="发消息" enabled="true" focusable="true"
        |                focused="false" index="2" instance="2"
        |                long-clickable="false" package="com.tencent.mobileqq"
        |                password="false"
        |                resource-id="com.tencent.mobileqq:id/name"
        |                scrollable="false" selected="false" text="发消息"/>
        |            </android.widget.LinearLayout>
        |          </android.widget.FrameLayout>
        |        </android.widget.FrameLayout>
        |      </android.widget.LinearLayout>
        |    </android.widget.RelativeLayout>
        |  </android.widget.FrameLayout>
        |</hierarchy>
        |
      """.stripMargin
    val dom=XPathUtil.toDocument(content)
    val xpath="string(//*[contains(@content-desc, '帐号信息') and @clickable='true']/@content-desc)"
    val res=XPathUtil.getNodeListByXPath(xpath, dom)

    log.info(res)
  }


  test("text xpath"){
    val value=XPathUtil.getNodeListByXPath("//*[@text='买什么']", domAndroid)
    value.foreach(log.info)
  }


  test("get back button"){


    val value1=XPathUtil.getNodeListByXPath("//*[@label='nav_icon_back']", domIOS)
    value1.foreach(log.info)

    val value2=XPathUtil.getNodeListByXPath("//UIANavigationBar/UIAButton[@label=\"nav_icon_back\"]", domIOS)
    value2.foreach(log.info)
  }

  test("同类型的控件是否具备selected=true属性"){
    val nodes=XPathUtil.getNodeListByXPath("//*[../*[@selected='true']]", domAndroid)
    nodes.foreach(x=>log.info(x.getOrElse("xpath", "")))

    log.info("两层以上")
    val nodes2=XPathUtil.getNodeListByXPath("//*[../../*[@selected='true']]", domAndroid)
    nodes2.foreach(x=>log.info(x.getOrElse("xpath", "")))
  }


  test("xpath"){
    XPathUtil.xpathExpr=List("resource-id", "content-desc", "depth", "selected")
    XPathUtil.getNodeListByXPath("//*", xmlAndroid).foreach(node=>{
      println(node.get("xpath"))
      println(node.get("depth"))
    })

  }

/*  test("sort selected nodes"){
    val xmlAndroid=Source.fromFile("/tmp/xueqiu/1520350511580/16_com.xueqiu.android-MainActivity_decor_content_parent-content-mainContent-public_timeline_content-lis.dom").mkString
    val crawler=new Crawler
    crawler.conf.sortByAttribute=List("depth", "selected")
    XPathUtil.xpathExpr=List("class", "text", "content-desc", "depth", "instance", "index", "selected")
    val map=mutable.HashMap[String, Boolean]()
    crawler.getSelectedNodes(XPathUtil.toDocument(xmlAndroid), true).foreach(node=>{
      if(node.get("selected").get=="true"){
        map(node.get("ancestor").get.toString)=true
        println(node.get("ancestor").get.toString)
      }

    })

    crawler.getSelectedNodes(XPathUtil.toDocument(xmlAndroid), true).foreach(node=>{
      println(node.get("depth").get)
      println(node.get("selected").get)
      println(map.getOrElse(node.get("ancestor").get.toString, false))
      println(node.get("ancestor").get)
      println(node.get("xpath").get)
      println(node.get("name").get)
    })

  }*/


  test("key"){
    val content=
      """
        |<hierarchy rotation="0">
        |  <android.widget.FrameLayout bounds="[0,1016][1080,1313]"
        |    checkable="false" checked="false" class="android.widget.FrameLayout"
        |    clickable="false" content-desc="" enabled="true" focusable="false"
        |    focused="false" index="0" instance="0" long-clickable="false"
        |    package="com.sinitek.rqkj.android" password="false" resource-id=""
        |    scrollable="false" selected="false" text="">
        |    <android.widget.LinearLayout bounds="[0,1016][1080,1313]"
        |      checkable="false" checked="false"
        |      class="android.widget.LinearLayout" clickable="false"
        |      content-desc="" enabled="true" focusable="false" focused="false"
        |      index="0" instance="0" long-clickable="false"
        |      package="com.sinitek.rqkj.android" password="false" resource-id=""
        |      scrollable="false" selected="false" text="">
        |      <android.widget.FrameLayout bounds="[0,1016][1080,1313]"
        |        checkable="false" checked="false"
        |        class="android.widget.FrameLayout" clickable="false"
        |        content-desc="" enabled="true" focusable="false" focused="false"
        |        index="0" instance="1" long-clickable="false"
        |        package="com.sinitek.rqkj.android" password="false"
        |        resource-id="android:id/content" scrollable="false"
        |        selected="false" text="">
        |        <android.widget.RelativeLayout bounds="[0,1016][1080,1313]"
        |          checkable="false" checked="false"
        |          class="android.widget.RelativeLayout" clickable="false"
        |          content-desc="" enabled="true" focusable="false"
        |          focused="false" index="0" instance="0" long-clickable="false"
        |          package="com.sinitek.rqkj.android" password="false"
        |          resource-id="" scrollable="false" selected="false" text="">
        |          <android.widget.LinearLayout bounds="[391,1046][688,1313]"
        |            checkable="false" checked="false"
        |            class="android.widget.LinearLayout" clickable="false"
        |            content-desc="" enabled="true" focusable="false"
        |            focused="false" index="0" instance="1"
        |            long-clickable="false" package="com.sinitek.rqkj.android"
        |            password="false" resource-id="" scrollable="false"
        |            selected="false" text="">
        |            <android.view.View bounds="[479,1076][599,1196]"
        |              checkable="false" checked="false"
        |              class="android.view.View" clickable="false"
        |              content-desc="" enabled="true" focusable="false"
        |              focused="false" index="0" instance="0"
        |              long-clickable="false" package="com.sinitek.rqkj.android"
        |              password="false" resource-id="" scrollable="false"
        |              selected="false" text=""/>
        |            <android.widget.TextView bounds="[460,1226][619,1283]"
        |              checkable="false" checked="false"
        |              class="android.widget.TextView" clickable="false"
        |              content-desc="" enabled="true" focusable="false"
        |              focused="false" index="1" instance="0"
        |              long-clickable="false" package="com.sinitek.rqkj.android"
        |              password="false" resource-id="" scrollable="false"
        |              selected="false" text="加载中..."/>
        |          </android.widget.LinearLayout>
        |        </android.widget.RelativeLayout>
        |      </android.widget.FrameLayout>
        |    </android.widget.LinearLayout>
        |  </android.widget.FrameLayout>
        |</hierarchy>
      """.stripMargin

    println(XPathUtil.getNodeListByKey("加载中...", XPathUtil.toDocument(content)))
  }

  test("pretty print"){
    println(XPathUtil.toPrettyXML("<xml><node><children>1</children><children>2</children></node></xml>"))
  }

  test("html"){
    val xmlHtml=
      """
        |<html class="expanded" xmlns="http://www.w3.org/1999/xhtml">
        |  <head>
        |    <META http-equiv="Content-Type" content="text/html; charset=UTF-8">   <!--STATUS OK-->
        |    <meta content="text/html;charset=utf-8" http-equiv="Content-Type">
        |    <meta content="IE=Edge,chrome=1" http-equiv="X-UA-Compatible">
        |    <link href="//gss0.bdstatic.com/5foIcy0a2gI2n2jgoY3K/static/fisp_static/common/img/favicon.ico" mce_href="../static/img/favicon.ico" rel="icon" type="image/x-icon">
        |    <title>百度新闻&mdash;&mdash;全球最大的中文新闻平台</title>
        |    <meta content="百度新闻是包含海量资讯的新闻服务平台，真实反映每时每刻的新闻热点。您可以搜索新闻事件、热点话题、人物动态、产品资讯等，快速了解它们的最新进展。" name="description" style="">
        |    <link href="//gss0.bdstatic.com/5foIcy0a2gI2n2jgoY3K/static/fisp_static/common/module_static_include/module_static_include_1dd83b0.css" rel="stylesheet" type="text/css">
        |    <link href="//gss0.bdstatic.com/5foIcy0a2gI2n2jgoY3K/static/fisp_static/news/focustop/focustop_6e4b3ae.css" rel="stylesheet" type="text/css">
        |  </head>
        |  <body style="zoom: 1;">
        |    <div class="clearfix" id="header-wrapper">
        |      <div alog-alias="hunter-userbar-start" alog-group="userbar" id="usrbar" style="font-family: Arial;">
        |        <ul>
        |          <li>
        |            <a href="http://app.news.baidu.com/?src=pctop" id="app_tooltip" mon="target=appLink" style="margin-right:20px;" target="_blank">百度新闻客户端       <div id="app_tooltip_qrcode">
        |                <img src="//gss0.bdstatic.com/5foIcy0a2gI2n2jgoY3K/static/fisp_static/common/img/sidebar/1014720a_d31158d.png">        </div>
        |            </a>
        |          </li>
        |          <li>
        |            <a href="http://passport.baidu.com/v2/?reg&amp;tpl=xw&amp;regType=1&amp;u=http%3A%2F%2Fnews.baidu.com%2F" id="passReg">注册</a>
        |          </li>
        |          <li>
        |            <a href="http://passport.baidu.com/v2/?login&amp;tpl=xw&amp;u=http%3A%2F%2Fnews.baidu.com%2F" id="passLog">登录</a>
        |          </li>
        |          <li>
        |            <a href="http://www.baidu.com" style="margin-right:11px;">百度首页</a>
        |          </li>
        |        </ul>
        |    <div id="headerwrapper">
        |      <div alog-alias="hunter-header-start" alog-group="header" id="header">
        |        <table alog-group="search-box" class="sbox" id="sbox">
        |          <tbody>
        |            <tr>
        |              <td class="logo">
        |                <div class="logo">
        |                  <a href="http://news.baidu.com/">           <!--[if !IE]><!--><img alt="百度新闻" height="46px" src="https://box.bdimg.com/static/fisp_static/common/img/searchbox/logo_news_276_88_1f9876a.png" width="137px">          <!--<![endif]-->           <!--[if IE 6]><img src="https://box.bdimg.com/static/fisp_static/common/img/searchbox/logo_news_276_88_for_ie6_1597c18.png" alt="百度新闻" height="46px" width="137px"><![endif]-->           <!--[if gt IE 6]><img src="https://box.bdimg.com/static/fisp_static/common/img/searchbox/logo_news_276_88_1f9876a.png" alt="百度新闻" height="46px" width="137px"><![endif]--> </a>
        |                </div>
        |                <div class="date"></div>
        |              </td>        <td class="search">
        |                <table>
        |                  <tbody>
        |                    <tr>
        |                      <td class="box">
        |                        <div id="sugarea">
        |                          <span class="s_ipt_wr" id="s_ipt_wr"><input autocomplete="off" class="word" id="ww" maxlength="100" name="word" size="42" tabindex="1">
        |                            <div id="sd_1537858713165" style="display: none; background-color: rgb(255, 255, 255);"></div>
        |                          </span>             <span class="s_btn_wr"><input class="btn" id="s_btn_wr" onmousedown="this.className='btn s_btn_h'" onmouseout="this.className='btn'" type="button" value="百度一下"></span>
        |                          <div></div>
        |                        </div>
        |                      </td>            <td class="help"><a href="//help.baidu.com">帮助</a><a href="//news.baidu.com/z/resource/pc/staticpage/advanced_news.html">高级搜索</a><a href="//news.baidu.com/z/resource/pc/staticpage/pianhao.html">设置</a></td>
        |                    </tr>
        |                  </tbody>
        |                </table>
        |                <p class="search-radios">
        |                  <input checked id="news" name="tn" type="radio" value="news"> <label class="checked" for="news">新闻全文</label> <input id="newstitle" name="tn" type="radio" value="newstitle"> <label class="not-checked" for="newstitle">新闻标题</label>
        |                </p>
        |                <input id="from" name="from" type="hidden" value="news"> <input id="cl" name="cl" type="hidden" value="2"> <input id="rn" name="rn" type="hidden" value="20"> <input id="ct" name="ct" type="hidden" value="1"> </td>
        |            </tr>
        |          </tbody>
        |        </table>
        |      </div>
        |      <div alog-group="home-menu" class="mod-navbar" id="menu">
        |        <div class="channel-shanghai clearfix" id="channel-shanghai" style="display: none; z-index: 9;">
        |          <div class="menu-list">
        |            <ul class="clearfix lavalamp" style="position: relative;">
        |              <div class="lavalamp-object" style="position: absolute; width: 0px; height: 0px; top: 0px; left: 0px;"></div>
        |              <li class="navitem-index current active lavalamp-item" style="z-index: 5; position: relative;">
        |                <a href="/">首页</a>
        |              </li>
        |              <li class="lavalamp-item" style="z-index: 5; position: relative;">
        |                <a href="/guonei">国内</a>
        |              </li>
        |              <li class="lavalamp-item" style="z-index: 5; position: relative;">
        |                <a href="/guoji">国际</a>
        |              </li>
        |              <li class="lavalamp-item" style="z-index: 5; position: relative;">
        |                <a href="/mil">军事</a>
        |              </li>
        |              <li class="lavalamp-item" style="z-index: 5; position: relative;">
        |                <a href="/finance">财经</a>
        |              </li>
        |              <li class="lavalamp-item" style="z-index: 5; position: relative;">
        |                <a href="/ent">娱乐</a>
        |              </li>
        |              <li class="lavalamp-item" style="z-index: 5; position: relative;">
        |                <a href="/sports">体育</a>
        |              </li>
        |              <li class="lavalamp-item" style="z-index: 5; position: relative;">
        |                <a href="/internet">互联网</a>
        |              </li>
        |              <li class="lavalamp-item" style="z-index: 5; position: relative;">
        |                <a href="/tech">科技</a>
        |              </li>
        |              <li class="lavalamp-item" style="z-index: 5; position: relative;">
        |                <a href="/game">游戏</a>
        |              </li>
        |              <li class="lavalamp-item" style="z-index: 5; position: relative;">
        |                <a href="/lady">女人</a>
        |              </li>
        |              <li class="lavalamp-item" style="z-index: 5; position: relative;">
        |                <a href="/auto">汽车</a>
        |              </li>
        |              <li class="lavalamp-item" style="z-index: 5; position: relative;">
        |                <a href="/house">房产</a>
        |              </li>
        |              <li class="lavalamp-item" style="z-index: 5; position: relative;">
        |                <a href="http://jian.news.baidu.com/" target="_blank">个性推荐</a>
        |              </li>
        |            </ul>
        |          </div>
        |          <i class="slogan"></i>
        |        </div>
        |        <div class="channel-all clearfix" id="channel-all" style="z-index: 9;">
        |          <div class="menu-list">
        |            <ul class="clearfix lavalamp" style="position: relative;">
        |              <div class="lavalamp-object" style="position: absolute; width: 68px; height: 40px; top: 0px; left: 0px; overflow: hidden;"></div>
        |              <li class="navitem-index current active lavalamp-item" style="z-index: 5; position: relative;">
        |                <a href="/">首页</a>
        |              </li>
        |              <li class="lavalamp-item" style="z-index: 5; position: relative;">
        |                <a href="/guonei">国内</a>
        |              </li>
        |              <li class="lavalamp-item" style="z-index: 5; position: relative;">
        |                <a href="/guoji">国际</a>
        |              </li>
        |              <li class="lavalamp-item" style="z-index: 5; position: relative;">
        |                <a href="/mil">军事</a>
        |              </li>
        |              <li class="lavalamp-item" style="z-index: 5; position: relative;">
        |                <a href="/finance">财经</a>
        |              </li>
        |              <li class="lavalamp-item" style="z-index: 5; position: relative;">
        |                <a href="/ent">娱乐</a>
        |              </li>
        |              <li class="lavalamp-item" style="z-index: 5; position: relative;">
        |                <a href="/sports">体育</a>
        |              </li>
        |              <li class="lavalamp-item" style="z-index: 5; position: relative;">
        |                <a href="/internet">互联网</a>
        |              </li>
        |              <li class="lavalamp-item" style="z-index: 5; position: relative;">
        |                <a href="/tech">科技</a>
        |              </li>
        |              <li class="lavalamp-item" style="z-index: 5; position: relative;">
        |                <a href="/game">游戏</a>
        |              </li>
        |              <li class="lavalamp-item" style="z-index: 5; position: relative;">
        |                <a href="/lady">女人</a>
        |              </li>
        |              <li class="lavalamp-item" style="z-index: 5; position: relative;">
        |                <a href="/auto">汽车</a>
        |              </li>
        |              <li class="lavalamp-item" style="z-index: 5; position: relative;">
        |                <a href="/house">房产</a>
        |              </li>
        |              <li class="lavalamp-item" style="z-index: 5; position: relative;">
        |                <a href="http://jian.news.baidu.com/" target="_blank">个性推荐</a>
        |              </li>
        |            </ul>
        |          </div>
        |          <i class="slogan"></i>
        |        </div>
        |      </div>
        |    </div>
        |    <div alog-alias="b" id="body">
        |      <div class="top-banner" id="topbanner"></div>
        |      <div class="column clearfix" id="focus-top" style="margin-top: 12px; margin-bottom: 31px;">
        |        <div alog-group="focus-top-left" class="l-left-col">
        |          <div id="left-col-wrapper">
        |            <div class="recommend-tip-wrapper">
        |              <div class="tip-wrapper">
        |                <div class="background-wrapper">
        |                  <a class="mod-headline-tip" href="javascript:void(0);" id="tip-float">
        |                    <div class="content-wrapper">
        |                      <i class="tip-logo"></i>
        |                      <div class="tip-content">            点击刷新，将会有未读推荐           </div>
        |                    </div>
        |                  </a>
        |                </div>
        |              </div>
        |            </div>
        |            <div class="mod-headline-tab" id="headline-tabs">
        |              <ul class="clearfix">
        |                <li class="active">
        |                  <a data-control="pane-news" href="javascript:void(0);">热点要闻</a>
        |                </li>
        |              </ul>
        |              <a class="tab-login" href="javascript:void(0);" id="tab-login" mon="m=53&amp;a=3" onclick="return false" style="display: inline;"></a>
        |            </div>
        |            <div class="mod-tab-content">
        |              <div class="mod-tab-pane active" id="pane-news">
        |                <div alog-group="focustop-hotnews" class="hotnews">
        |                  <ul>
        |                    <li class="hdline0">
        |                      <i class="dot"></i> <strong> <a class="a3" href="http://news.cctv.com/2018/09/24/ARTIXENvresdj75GlgxMi40h180924.shtml" mon="ct=1&amp;a=1&amp;c=top&amp;pn=0" target="_blank">习近平再谈乡村振兴战略，这几点很重要</a></strong>
        |                    </li>
        |                    <li class="hdline1">
        |                      <i class="dot"></i> <strong> <a href="http://www.xinhuanet.com//2018-09/24/c_1123475262.htm" mon="r=1" target="_blank">《关于中美经贸摩擦的事实与中方立场》白皮书发布 </a> </strong>
        |                    </li>
        |                    <li class="hdline2">
        |                      <i class="dot"></i> <strong> <a class="a3" href="http://www.xinhuanet.com/2018-09/24/c_1123475871.htm" mon="ct=1&amp;a=1&amp;c=top&amp;pn=1" target="_blank">白皮书彰显中国应对经贸摩擦的坚定、沉着与理性 </a></strong>
        |                    </li>
        |                    <li class="hdline3">
        |                      <i class="dot"></i> <strong> <a href="http://ydyl.people.com.cn/n1/2018/0925/c411837-30311383.html" mon="r=1" target="_blank">让白皮书告诉世界（侠客岛&middot;解局）</a> </strong>
        |                    </li>
        |                    <li class="hdline4">
        |                      <i class="dot"></i> <strong> <a class="a3" href="http://opinion.people.com.cn/n1/2018/0924/c1003-30310420.html" mon="ct=1&amp;a=1&amp;c=top&amp;pn=2" target="_blank">合作是处理中美经贸摩擦唯一正确选择</a></strong>
        |                    </li>
        |                    <li class="hdline5">
        |                      <i class="dot"></i> <strong> <a href="http://world.huanqiu.com/exclusive/2018-09/13104883.html" mon="r=1" target="_blank">商务部副部长：把刀架在中国脖子上的谈判毫无诚意</a> </strong>
        |                    </li>
        |                  </ul>
        |                </div>
        |                <ul class="ulist focuslistnews">
        |                  <li class="bold-item">
        |                    <span class="dot"></span> <a href="https://3w.huanqiu.com/a/6295b6/7GtNSYge9MI?agt=8" mon="ct=1&amp;a=2&amp;c=top&amp;pn=1" target="_blank">深圳将成直辖市?官方辟谣:传言毫无根据纯属猜测!</a>
        |                  </li>
        |                  <li>
        |                    <a href="http://news.ifeng.com/a/20180925/60084415_0.shtml?_zbs_baidu_news" mon="ct=1&amp;a=2&amp;c=top&amp;pn=2" target="_blank">安徽宣城:降低购房首付和贷款利率以鼓励生育</a>
        |                  </li>
        |                  <li>
        |                    <a href="http://news.ifeng.com/a/20180925/60083700_0.shtml?_zbs_baidu_news" mon="ct=1&amp;a=2&amp;c=top&amp;pn=3" target="_blank">党员注意!微信上这些信息不能发,严重者开除党籍!</a>
        |                  </li>
        |                  <li>
        |                    <a href="https://3w.huanqiu.com/a/ec157d/7Gu5ChLBbd6?agt=8" mon="ct=1&amp;a=2&amp;c=top&amp;pn=4" target="_blank">看事实!有关中美经贸的这些错误认知,刚刚中方逐条驳斥</a>
        |                  </li>
        |                  <li>
        |                    <a href="https://3w.huanqiu.com/a/a4d1ef/7GtMZ9Nrhqo?agt=8" mon="ct=1&amp;a=2&amp;c=top&amp;pn=5" target="_blank">京津冀部分地区将有轻度霾 29日起大气扩散条件转好</a>
        |                  </li>
        |                  <li>
        |                    <a href="http://news.ifeng.com/a/20180925/60083472_0.shtml?_zbs_baidu_news" mon="ct=1&amp;a=2&amp;c=top&amp;pn=6" target="_blank">中国使馆:决不接受瑞典电视台狡辩和避重就轻的"道歉"</a>
        |                  </li>
        |                </ul>
        |                <ul class="ulist focuslistnews">
        |                  <li class="bold-item">
        |                    <span class="dot"></span> <a href="http://news.ifeng.com/a/20180925/60083890_0.shtml?_zbs_baidu_news" mon="ct=1&amp;a=2&amp;c=top&amp;pn=7" target="_blank">这四种交通罚单请在15天内缴纳罚款,否则罚金翻倍</a>
        |                  </li>
        |                  <li>
        |                    <a href="http://news.ifeng.com/a/20180925/60083943_0.shtml?_zbs_baidu_news" mon="ct=1&amp;a=2&amp;c=top&amp;pn=8" target="_blank">10月起,这些新规将影响你的生活!还有一个好消息</a>
        |                  </li>
        |                  <li>
        |                    <a href="http://ent.ifeng.com/a/20180925/43110443_0.shtml?_zbs_baidu_news" mon="ct=1&amp;a=2&amp;c=top&amp;pn=9" target="_blank">广电总局局长对湖南广电提出要求:调控治理真人秀</a>
        |                  </li>
        |                  <li>
        |                    <a href="http://xinwen.eastday.com/a/n180925093410987.html?qid=news.baidu.com" mon="ct=1&amp;a=2&amp;c=top&amp;pn=10" target="_blank">多个热点城市新房降价促销,四季度楼市大概率降温</a>
        |                  </li>
        |                  <li>
        |                    <a href="http://news.ifeng.com/a/20180925/60084380_0.shtml?_zbs_baidu_news" mon="ct=1&amp;a=2&amp;c=top&amp;pn=11" target="_blank">河南检方回应"未成年强奸案冰释前嫌":用词不当 考虑问责</a>
        |                  </li>
        |                  <li>
        |                    <a href="https://3w.huanqiu.com/a/c36dc8/7GtZosRJhsc?agt=8" mon="ct=1&amp;a=2&amp;c=top&amp;pn=12" target="_blank">这个"台湾省爱国教育基地"被强拆前,再一次升起了五星红旗</a>
        |                  </li>
        |                </ul>
        |                <ul class="ulist focuslistnews">
        |                  <li class="bold-item">
        |                    <span class="dot"></span> <a href="http://news.ifeng.com/a/20180925/60083726_0.shtml?_zbs_baidu_news" mon="ct=1&amp;a=2&amp;c=top&amp;pn=13" target="_blank">又踩&ldquo;一中&rdquo;红线!美国务院批准对台22亿元军售</a>
        |                  </li>
        |                  <li>
        |                    <a href="http://xinwen.eastday.com/a/n180925072504121.html?qid=news.baidu.com" mon="ct=1&amp;a=2&amp;c=top&amp;pn=14" target="_blank">英国政府11月或提前大选 企业囤货以备&ldquo;硬脱欧&rdquo;</a>
        |                  </li>
        |                  <li>
        |                    <a href="https://3w.huanqiu.com/a/c36dc8/7GtQT8qoUqQ?agt=8" mon="ct=1&amp;a=2&amp;c=top&amp;pn=15" target="_blank">无视特朗普威胁，欧盟出重招力挺伊朗石油出口</a>
        |                  </li>
        |                  <li>
        |                    <a href="http://xinwen.eastday.com/a/180925093053116.html?qid=news.baidu.com" mon="ct=1&amp;a=2&amp;c=top&amp;pn=16" target="_blank">朴槿惠中秋无人探视 监狱送130克零食当过节福利</a>
        |                  </li>
        |                  <li>
        |                    <a href="http://news.ifeng.com/a/20180925/60084362_0.shtml?_zbs_baidu_news" mon="ct=1&amp;a=2&amp;c=top&amp;pn=17" target="_blank"> 洪都拉斯总统:美国减援助,中美洲台"邦交国"或转向大陆</a>
        |                  </li>
        |                  <li>
        |                    <a href="https://3w.huanqiu.com/a/21eee3/7GtPeRIbWCs?agt=8" mon="ct=1&amp;a=2&amp;c=top&amp;pn=18" target="_blank">特朗普坐橡皮艇&ldquo;救人&rdquo;照片疯传 美媒戳穿:图是P的</a>
        |                  </li>
        |                </ul>
        |                <ul class="ulist focuslistnews">
        |                  <li class="bold-item">
        |                    <span class="dot"></span> <a href="http://xinwen.eastday.com/a/n180925071235685.html?qid=news.baidu.com" mon="ct=1&amp;a=2&amp;c=top&amp;pn=19" target="_blank">今年最受欢迎月饼竟是它?网友:此一时彼一时啊!</a>
        |                  </li>
        |                  <li>
        |                    <a href="https://3w.huanqiu.com/a/c4b13d/7Gu5Xwdass8?agt=8" mon="ct=1&amp;a=2&amp;c=top&amp;pn=20" target="_blank">地铁也霸座?北京地铁10号线早高峰一男子躺占4个座</a>
        |                  </li>
        |                  <li>
        |                    <a href="https://3w.huanqiu.com/a/1080fe/7GtQAjJUN6o?agt=8" mon="ct=1&amp;a=2&amp;c=top&amp;pn=21" target="_blank">央视财经评论:楼市乱象怎么破?先给"黑中介"刮骨疗毒</a>
        |                  </li>
        |                  <li>
        |                    <a href="http://xinwen.eastday.com/a/n180925072236735.html?qid=news.baidu.com" mon="ct=1&amp;a=2&amp;c=top&amp;pn=22" target="_blank">秦岭保卫战:40天拆百万平米 有别墅&ldquo;无人认领&rdquo;</a>
        |                  </li>
        |                  <li>
        |                    <a href="http://xinwen.eastday.com/a/n180925114512426.html?qid=news.baidu.com" mon="ct=1&amp;a=2&amp;c=top&amp;pn=23" target="_blank">卖"假阳澄湖大闸蟹"的要注意了 犯这个罪最高可判无期</a>
        |                  </li>
        |                  <li>
        |                    <a href="http://xinwen.eastday.com/a/n180925100022030.html?qid=news.baidu.com" mon="ct=1&amp;a=2&amp;c=top&amp;pn=24" target="_blank">幼儿园装修孩子流鼻血 园方家长2次检测结果却相反</a>
        |                  </li>
        |                </ul>
        |                <ul class="ulist focuslistnews">
        |                  <li class="bold-item">
        |                    <span class="dot"></span> <a href="https://3w.huanqiu.com/a/b7f8f5/7GtO5lKSGv6?agt=8" mon="ct=1&amp;a=2&amp;c=top&amp;pn=25" target="_blank">中国家长烦心事:天价补课费奇葩作业和变异家长群</a>
        |                  </li>
        |                  <li>
        |                    <a href="http://news.ifeng.com/a/20180925/60084326_0.shtml?_zbs_baidu_news" mon="ct=1&amp;a=2&amp;c=top&amp;pn=26" target="_blank">安徽宿州开闸泄洪下游毫不知情 致江苏洪泽湖鱼蟹死亡</a>
        |                  </li>
        |                  <li>
        |                    <a href="http://xinwen.eastday.com/a/180925092901149.html?qid=news.baidu.com" mon="ct=1&amp;a=2&amp;c=top&amp;pn=27" target="_blank">广东敬老院院长侵吞3587万"活命钱" 用橡皮擦篡改金额</a>
        |                  </li>
        |                  <li>
        |                    <a href="http://xinwen.eastday.com/a/180925072351858.html?qid=news.baidu.com" mon="ct=1&amp;a=2&amp;c=top&amp;pn=28" target="_blank">警方雨夜7小时救女大学生未获感谢?女生:未致谢</a>
        |                  </li>
        |                  <li>
        |                    <a href="http://top.chinadaily.com.cn/2018-09/25/content_36971533.htm" mon="ct=1&amp;a=2&amp;c=top&amp;pn=29" target="_blank">电商法出台，消费维权之路并没有&ldquo;更容易&rdquo;</a>
        |                  </li>
        |                  <li>
        |                    <a href="https://kandian.youth.cn/index/detail?sign=6d85BbPoKr24xAg" mon="ct=1&amp;a=2&amp;c=top&amp;pn=30" target="_blank">5小时,30000次心脏按压,30名医生轮流抢救8岁男孩</a>
        |                  </li>
        |                </ul>
        |              </div>
        |              <div class="mod-tab-pane pane-recommend " id="pane-recommend">
        |                <div class="mod-tab-loading">
        |                  <i class="icon-loading"></i>
        |                  <p class="desc">加载中请您耐心等待...</p>
        |                </div>
        |                <div class="tip-wrapper">
        |                  <a class="mod-headline-tip" href="javascript:void(0);" id="tip" mon="m=53&amp;a=5"> <i class="tip-logo"></i>
        |                    <div class="tip-content">           点击刷新，将会有未读推荐          </div>
        |                  </a>
        |                </div>
        |                <div class="feeds" id="feeds"></div>
        |                <div class="feeds-more" id="feeds-more">
        |                  <a href="javascript:void(0);" mon="m=53&amp;a=4" onclick="return false"><span>更多个性推荐新闻</span></a>
        |                </div>
        |              </div>
        |            </div>
        |          </div>
        |        </div>
        |        <div alog-group="focus-top-right" class="l-right-col">
        |          <div alog-group="focustop-carousel" class="toparea-aside-top">
        |            <div class="imgplayer clearfix" id="imgplayer">
        |              <div class="carousel-control" id="imgplayer-control">
        |                <a class="carousel-btn-prev" href="javascript:void(0);" id="imgplayer-prev" mon="c=top&amp;a=50&amp;col=4&amp;ct=1&amp;pn=0"> <span class="icon-wrap"></span> </a>         <a class="carousel-btn-next" href="javascript:void(0);" id="imgplayer-next" mon="c=top&amp;a=52&amp;col=4&amp;ct=1&amp;pn=0"> <span class="icon-wrap"></span> </a>
        |              </div>
        |              <div class="imgview" id="imgView">
        |                <a href="https://3w.huanqiu.com/a/3458fa/7GtEBqSYxNu?agt=8" mon="c=top&amp;a=12&amp;col=4&amp;pn=5" target="_blank"><img src="https://imgsa.baidu.com/news/q%3D100/sign=b0fa05670423dd542773a368e108b3df/a5c27d1ed21b0ef4603ce15dd0c451da80cb3ed8.jpg"></a>
        |              </div>
        |              <div class="imgnav-mask"></div>
        |              <div class="imgnav" id="imgNav">
        |                <a class="" href="javascript:void(0);" index="8" mon="c=top&amp;a=51&amp;col=4&amp;ct=1&amp;pn=8">8</a>         <a class="" href="javascript:void(0);" index="7" mon="c=top&amp;a=51&amp;col=4&amp;ct=1&amp;pn=7">7</a>         <a class="" href="javascript:void(0);" index="6" mon="c=top&amp;a=51&amp;col=4&amp;ct=1&amp;pn=6">6</a>         <a class="active" href="javascript:void(0);" index="5" mon="c=top&amp;a=51&amp;col=4&amp;ct=1&amp;pn=5">5</a>         <a class="" href="javascript:void(0);" index="4" mon="c=top&amp;a=51&amp;col=4&amp;ct=1&amp;pn=4">4</a>         <a class="" href="javascript:void(0);" index="3" mon="c=top&amp;a=51&amp;col=4&amp;ct=1&amp;pn=3">3</a>         <a class="" href="javascript:void(0);" index="2" mon="c=top&amp;a=51&amp;col=4&amp;ct=1&amp;pn=2">2</a>         <a class="" href="javascript:void(0);" index="1" mon="c=top&amp;a=51&amp;col=4&amp;ct=1&amp;pn=1">1</a>
        |              </div>
        |              <div class="imgtit" id="imgTitle">
        |                <a href="https://3w.huanqiu.com/a/3458fa/7GtEBqSYxNu?agt=8" mon="col=4&amp;a=9&amp;ct=1&amp;pn=5" target="_blank"><strong>&ldquo;空中拼刺刀&rdquo;新传奇&mdash;&mdash;空军&ldquo;先锋飞行大队&rdquo;</strong></a>
        |              </div>
        |            </div>
        |            <ul class="sub_19da">
        |              <a class="home-banner-cell left" href="http://topics.gmw.cn/node_114815.htm"></a>        <a class="home-banner-cell right" href="http://www.qstheory.cn/zt2017/xcgcdd19djs/index.htm"></a>
        |            </ul>
        |            <div class="sda_line">
        |            </div>
        |          </div>
        |          <div alog-group="focus-top-news-hotwords">
        |            <div class="mod h-bd-box" id="news-hotwords">
        |              <div class="hd line">
        |                <h3>热搜新闻词<span class="en">HOT WORDS</span>
        |                </h3>
        |              </div>
        |              <div class="bd">
        |                <ul class="hotwords clearfix">
        |                  <li class="li_0 li_color_0 button-slide">
        |                    <a class="hotwords_li_a" href="https://www.baidu.com/s?wd=%E4%B8%AD%E7%A7%8B%E4%BD%B3%E8%8A%82%EF%BC%8C%E5%90%AC%E4%B9%A0%E8%BF%91%E5%B9%B3%E8%AE%B2%E4%BB%80%E4%B9%88%E6%98%AF%E4%B9%A1%E6%84%81" mon="ct=1&amp;c=top&amp;a=30&amp;pn=1" style="top: 13.5px;" target="_blank" title="中秋佳节，听习近平讲什么是乡愁">中秋佳节，<br>听习近平讲什么是乡愁</a>
        |                  </li>
        |                  <li class="li_1 li_color_1 button-slide">
        |                    <a class="hotwords_li_a" href="https://www.baidu.com/s?wd=%E4%B9%A0%E8%BF%91%E5%B9%B3%E4%B8%BB%E6%8C%81%E4%B8%AD%E5%85%B1%E4%B8%AD%E5%A4%AE%E6%94%BF%E6%B2%BB%E5%B1%80%E4%BC%9A%E8%AE%AE" mon="ct=1&amp;c=top&amp;a=30&amp;pn=2" style="top: 23px;" target="_blank" title="习近平主持中共中央政治局会议">习近平主持<br>中共中央政治局会议</a>
        |                  </li>
        |                  <li class="li_2 li_color_2 button-slide">
        |                    <a class="hotwords_li_a" href="https://www.baidu.com/s?wd=%E4%B8%AD%E5%9B%BD%E5%AF%B9%E5%A4%96%E8%B4%B8%E6%98%93%E5%AE%9E%E7%8E%B0%E5%8E%86%E5%8F%B2%E6%80%A7%E8%B7%A8%E8%B6%8A" mon="ct=1&amp;c=top&amp;a=30&amp;pn=3" style="top: 29px;" target="_blank" title="中国对外贸易实现历史性跨越">中国对外贸易<br>实现历史性跨越</a>
        |                  </li>
        |                  <li class="li_3 li_color_3 button-slide">
        |                    <a class="hotwords_li_a" href="https://www.baidu.com/s?wd=%E6%B7%B1%E5%9C%B3%E7%9B%B4%E8%BE%96%E4%BC%A0%E8%A8%80%E6%AF%AB%E6%97%A0%E6%A0%B9%E6%8D%AE" mon="ct=1&amp;c=top&amp;a=30&amp;pn=4" style="top: 29px;" target="_blank" title="深圳直辖传言毫无根据">深圳直辖传言<br>毫无根据</a>
        |                  </li>
        |                  <li class="li_4 li_color_4 button-slide">
        |                    <a class="hotwords_li_a" href="https://www.baidu.com/s?wd=%E6%A5%BC%E5%B8%82%E2%80%9C%E9%87%91%E4%B9%9D%E2%80%9D%E8%90%BD%E7%A9%BA%20%E5%9B%9B%E5%AD%A3%E5%BA%A6%E5%A4%A7%E6%A6%82%E7%8E%87%E9%99%8D%E6%B8%A9" mon="ct=1&amp;c=top&amp;a=30&amp;pn=5" style="top: 29px;" target="_blank" title="楼市&ldquo;金九&rdquo;落空">楼市&ldquo;金九&rdquo;<br>落空</a>
        |                  </li>
        |                  <li class="li_5 li_color_5 button-slide">
        |                    <a class="hotwords_li_a" href="https://www.baidu.com/s?wd=S-300%E9%98%B2%E7%A9%BA%E7%B3%BB%E7%BB%9F" mon="ct=1&amp;c=top&amp;a=30&amp;pn=6" style="top: 29px;" target="_blank" title="S-300防空系统">S-300<br>防空系统</a>
        |                  </li>
        |                  <li class="li_6 li_color_6 button-slide">
        |                    <a class="hotwords_li_a" href="https://www.baidu.com/s?wd=%E6%B1%BD%E8%BD%A6%E9%99%AA%E7%BB%83%E4%B9%B1%E8%B1%A1%E4%B8%9B%E7%94%9F" mon="ct=1&amp;c=top&amp;a=30&amp;pn=7" style="top: 29px;" target="_blank" title="汽车陪练乱象丛生">汽车陪练<br>乱象丛生</a>
        |                  </li>
        |                  <li class="li_7 li_color_7 button-slide">
        |                    <a class="hotwords_li_a" href="https://www.baidu.com/s?wd=%E4%BB%8A%E5%B9%B4%E6%9C%80%E5%8F%97%E6%AC%A2%E8%BF%8E%E6%9C%88%E9%A5%BC" mon="ct=1&amp;c=top&amp;a=30&amp;pn=8" style="top: 29px;" target="_blank" title="今年最受欢迎的月饼">今年最受<br>欢迎的月饼</a>
        |                  </li>
        |                  <li class="li_8 li_color_8 button-slide">
        |                    <a class="hotwords_li_a" href="https://www.baidu.com/s?wd=%E5%BE%AE%E4%BF%A1%E4%B8%8A%E8%BF%99%E4%BA%9B%E4%BF%A1%E6%81%AF%E4%B8%8D%E8%83%BD%E5%8F%91%2C%E4%B8%A5%E9%87%8D%E8%80%85%E5%BC%80%E9%99%A4%E5%85%9A%E7%B1%8D" mon="ct=1&amp;c=top&amp;a=30&amp;pn=9" style="top: 29px;" target="_blank" title="微信这些信息不能发">微信这些<br>信息不能发</a>
        |                  </li>
        |                  <li class="li_9 li_color_9 button-slide">
        |                    <a class="hotwords_li_a" href="https://www.baidu.com/s?wd=2018%E4%B8%96%E7%95%8C%E8%B6%B3%E7%90%83%E5%85%88%E7%94%9F" mon="ct=1&amp;c=top&amp;a=30&amp;pn=10" style="top: 29px;" target="_blank" title="2018世界足球先生">2018世界<br>足球先生</a>
        |                  </li>
        |                </ul>
        |              </div>
        |            </div>
        |          </div>
        |          <div alog-group="log-baijia" class="mod-baijia column clearfix" id="baijia">
        |            <div class="column-title-home">
        |              <div class="column-title-border">
        |                <h2>
        |                  <span class="column-title">百家号</span> <span class="en">BAIJIA</span>
        |                </h2>
        |                <div class="sub-class">
        |                </div>
        |              </div>
        |            </div>
        |            <div alog-group="log-baijia-left-up" class="l-middle-col" style="height:305px;">
        |              <div class="imagearea">
        |                <div class="imagearea-top" style="height:164px;">
        |                  <div class="image-mask-item">
        |                    <a class="item-image" href="http://baijiahao.baidu.com/s?id=1612473311948321182" mon="&amp;a=12" style="background-image:url(http://hiphotos.baidu.com/news/crop%3D80%2C0%2C413%2C277%3Bq%3D80%3B/sign=cd605c8a4b2309f7f320f7524f363cdf/3812b31bb051f81904324a54d7b44aed2e73e79d.jpg)" target="_blank" title="刘强东性侵案女主微信消息曝光"></a>           <a class="item-title" href="http://baijiahao.baidu.com/s?id=1612473311948321182" mon="&amp;a=9" target="_blank" title="刘强东性侵案女主微信消息曝光">刘强东性侵案女主微信消息曝光</a>
        |                  </div>
        |                </div>
        |                <div class="imagearea-bottom">
        |                  <div class="image-list-item">
        |                    <a class="img" href="http://baijiahao.baidu.com/s?id=1612537937008473288" mon="&amp;a=12" style="background-image:url(http://hiphotos.baidu.com/news/crop%3D0%2C0%2C492%2C330%3Bq%3D80%3B/sign=d30626dd306d55fbd1892c665012637a/b90e7bec54e736d1dabfc60b96504fc2d5626901.jpg)" target="_blank" title="黎曼猜想证明现场：困惑与沉默"></a>          <a class="txt" href="http://baijiahao.baidu.com/s?id=1612537937008473288" mon="&amp;a=9" target="_blank">黎曼猜想证明现场：困惑与沉默</a>
        |                  </div>
        |                  <div class="image-list-item">
        |                    <a class="img" href="http://baijiahao.baidu.com/s?id=1612448002174032247" mon="&amp;a=12" style="background-image:url(http://hiphotos.baidu.com/news/crop%3D40%2C0%2C443%2C263%3Bq%3D80%3B/sign=e427393d76ec54e755a3405e840cab7c/6159252dd42a2834f183820856b5c9ea15cebf88.jpg)" target="_blank" title="三无外卖店上线美团：只需1200元"></a>          <a class="txt" href="http://baijiahao.baidu.com/s?id=1612448002174032247" mon="&amp;a=9" target="_blank">三无外卖店上线美团：只需1200元</a>
        |                  </div>
        |                </div>
        |              </div>
        |            </div>
        |            <div alog-group="log-baijia-right-up" class="l-right-col" style="width:290px;">
        |              <div class="baijia-focus-list">
        |                <ul class="ulist bdlist">
        |                  <li class="bold-item">
        |                    <a href="http://baijiahao.baidu.com/s?id=1612543340219350969" mon="a=9" target="_blank">Android十年成长史</a>
        |                  </li>
        |                  <li>
        |                    <a href="http://baijiahao.baidu.com/s?id=1612544293285389275" mon="a=9" target="_blank">iPhone XsXs Max再陷&ldquo;信号门&rdquo;</a>
        |                  </li>
        |                  <li>
        |                    <a href="http://baijiahao.baidu.com/s?id=1612546481097441609" mon="a=9" target="_blank">左派精英消亡史：他们如何成为堕落天使</a>
        |                  </li>
        |                  <li>
        |                    <a href="http://baijiahao.baidu.com/s?id=1612545212882693133" mon="a=9" target="_blank">隐私保护，苹果产品的核心价值</a>
        |                  </li>
        |                  <li>
        |                    <a href="http://baijiahao.baidu.com/s?id=1612543287330022664" mon="a=9" target="_blank">ofo：&ldquo;ofo利用GSE进行融资&rdquo;消息不实</a>
        |                  </li>
        |                </ul>
        |                <ul class="ulist bdlist" style="padding-top:5px">
        |                  <li class="bold-item">
        |                    <a href="http://baijiahao.baidu.com/s?id=1612543937579334105" mon="a=9" target="_blank">Instagram CEO和CTO双双离职</a>
        |                  </li>
        |                  <li>
        |                    <a href="http://baijiahao.baidu.com/s?id=1612535247477428180" mon="a=9" target="_blank">亏损暴涨300%！小牛电动缘何登陆美股？</a>
        |                  </li>
        |                  <li>
        |                    <a href="http://baijiahao.baidu.com/s?id=1612409771218700967" mon="a=9" target="_blank">AI公司VC化：一边融资，一边投资</a>
        |                  </li>
        |                  <li>
        |                    <a href="http://baijiahao.baidu.com/s?id=1612515154433663650" mon="a=9" target="_blank">闻声识歌！苹果正式完成收购Shazam</a>
        |                  </li>
        |                  <li>
        |                    <a href="http://baijiahao.baidu.com/s?id=1612534293976350185" mon="a=9" target="_blank">今日头条试水金融遭举报</a>
        |                  </li>
        |                </ul>
        |                <ul class="ulist bdlist" style="padding-top:5px">
        |                  <li class="bold-item">
        |                    <a href="http://baijiahao.baidu.com/s?id=1612535706748553798" mon="a=9" target="_blank">美团外卖回应传闻：将尽快落实整改</a>
        |                  </li>
        |                  <li>
        |                    <a href="http://baijiahao.baidu.com/s?id=1612521631229102565" mon="a=9" target="_blank">中移动8元&ldquo;保号套餐&rdquo;回归 低门槛方便用户</a>
        |                  </li>
        |                  <li>
        |                    <a href="http://baijiahao.baidu.com/s?id=1612547672964426750" mon="a=9" target="_blank">科大讯飞&ldquo;AI同传造假&rdquo;和解：没主动造假</a>
        |                  </li>
        |                  <li>
        |                    <a href="http://baijiahao.baidu.com/s?id=1612533099329456626" mon="a=9" target="_blank">京东收盘暴跌达7.47%，创两年来新低</a>
        |                  </li>
        |                  <li>
        |                    <a href="http://baijiahao.baidu.com/s?id=1612449320862123437" mon="a=9" target="_blank">新iPhone发布后中国供应商百态</a>
        |                  </li>
        |                  <li>
        |                    <a href="http://baijiahao.baidu.com/s?id=1612533838498657587" mon="a=9" target="_blank">流媒体音乐鼻祖Pandora 35亿美元卖身</a>
        |                  </li>
        |                </ul>
        |              </div>
        |            </div>
        |            <div alog-group="log-baijia-left-down" class="l-middle-col">
        |              <div class="mod tbox" id="baijia-aside-recommend">
        |                <div class="bd" style="position:relative;height:160px;overflow:hidden;">
        |                  <div class="imagearea">
        |                    <div class="imagearea-top">
        |                      <div class="image-mask-item">
        |                        <a class="item-image" href="http://baijiahao.baidu.com/s?id=1612489196620271357" mon="&amp;a=12" style="background-image:url(http://hiphotos.baidu.com/news/crop%3D31%2C0%2C447%2C300%3Bq%3D80%3B/sign=b82fd6d43bd12f2eda4af42072f1e44e/d62a6059252dd42aebf7b6a80e3b5bb5c9eab833.jpg)" target="_blank" title="滴滴司机调研：到底谁才是事故频发的原罪"></a>             <a class="item-title" href="http://baijiahao.baidu.com/s?id=1612489196620271357" mon="&amp;a=9" target="_blank" title="滴滴司机调研：到底谁才是事故频发的原罪">滴滴司机调研：到底谁才是事故频发的原罪</a>
        |                      </div>
        |                    </div>
        |                  </div>
        |                </div>
        |              </div>
        |            </div>
        |          </div>
        |        </div>
        |      </div>
        |      <div class="mod-localnews column clearfix" id="local_news">
        |        <div alog-group="log-mil-title" class="column-title-home">
        |          <div class="column-title-border">
        |            <h2>
        |              <span id="city_name"><b>北京</b>新闻</span><span class="cname">LOCAL NEWS</span>
        |            </h2>
        |            <div class="localnews_logo" id="localnews_logo"></div>
        |            <a class="select-btn" id="change-city">切换城市</a>       <span id="p-more-link"></span>
        |          </div>
        |        </div>
        |        <div alog-group="log-local-left" class="l-left-col col-mod">
        |          <ul class="ulist focuslistnews" id="localnews-focus">
        |            <li class="bold-item">
        |              <span class="dot"></span><a href="http://ln.sina.com.cn/news/m/2018-09-25/detail-ihkmwytn9822948.shtml" mon="c=civilnews&amp;ct=0&amp;a=27&amp;col=8&amp;locname=%E5%8C%97%E4%BA%AC&amp;locid=0" target="_blank">朝阳县携手北京新发地集团推进县域产业振兴发展&mdash;...</a>
        |            </li>
        |            <li>
        |              <a href="http://bj.bendibao.com/news/2018925/252960.shtm" mon="c=civilnews&amp;ct=0&amp;a=27&amp;col=8&amp;locname=%E5%8C%97%E4%BA%AC&amp;locid=0" target="_blank">2018北京国庆节限行及交通提醒</a>
        |            </li>
        |            <li>
        |              <a href="http://news.ynet.com/2018/09/25/1433535t70.html" mon="c=civilnews&amp;ct=0&amp;a=27&amp;col=8&amp;locname=%E5%8C%97%E4%BA%AC&amp;locid=0" target="_blank">东京与北京：18世纪的&ldquo;两都赋&rdquo;</a>
        |            </li>
        |            <li>
        |              <a href="http://news.ynet.com/2018/09/25/1439433t70.html" mon="c=civilnews&amp;ct=0&amp;a=27&amp;col=8&amp;locname=%E5%8C%97%E4%BA%AC&amp;locid=0" target="_blank">节后赶上4、9限行严重拥堵 明起北京交通场站周...</a>
        |            </li>
        |            <li>
        |              <a href="http://finance.sina.com.cn/zl/china/2018-09-25/zl-ihkmwytn9549945.shtml#J_Comment_Wrap" mon="c=civilnews&amp;ct=0&amp;a=27&amp;col=8&amp;locname=%E5%8C%97%E4%BA%AC&amp;locid=0" target="_blank">你为什么没有爱情：高房价正在毁掉我们的幸福</a>
        |            </li>
        |            <li>
        |              <a href="http://finance.eastmoney.com/news/1354,20180925951649038.html" mon="c=civilnews&amp;ct=0&amp;a=27&amp;col=8&amp;locname=%E5%8C%97%E4%BA%AC&amp;locid=0" target="_blank">金融街业绩颓势尽显 踏错节奏频拿&ldquo;高价地&rdquo;</a>
        |            </li>
        |            <li>
        |              <a href="http://edu.sina.cn/zxx/zkzx/2018-09-25/detail-ihkmwytp0122243.d.html?vt=4&amp;cid=78164" mon="c=civilnews&amp;ct=0&amp;a=27&amp;col=8&amp;locname=%E5%8C%97%E4%BA%AC&amp;locid=0" target="_blank">2019北京中考各科分值是多少 选科如何折分？</a>
        |            </li>
        |            <li>
        |              <a href="http://tech.ifeng.com/a/20180925/45179644_0.shtml" mon="c=civilnews&amp;ct=0&amp;a=27&amp;col=8&amp;locname=%E5%8C%97%E4%BA%AC&amp;locid=0" target="_blank">58、赶集、安居客被责令暂停发布北京房源信息</a>
        |            </li>
        |          </ul>
        |        </div>
        |        <div alog-group="log-local-middle" class="l-middle-col">
        |          <div class="mod">
        |            <div class="hd">
        |              <h3>新闻图片</h3>
        |            </div>
        |            <div class="bd">
        |              <div class="imagearea" id="local_default" style="display: none;">
        |                <div class="imagearea-top">
        |                  <div class="image-mask-item">
        |                    <a class="item-image" href="" mon="&amp;pn=1&amp;a=12" target="_blank" title=""><img alt="" src=""></a>           <a class="item-title" href="" mon="&amp;pn=1&amp;a=9" target="_blank" title=""></a>
        |                  </div>
        |                </div>
        |              </div>
        |              <div class="imagearea" id="local_current" style="">
        |                <div class="imagearea-top" id="localnews-pic">
        |                  <div class="image-mask-item">
        |                    <a class="item-image" href="http://finance.ifeng.com/a/20180925/16522184_0.shtml" mon="c=civilnews&amp;ct=0&amp;a=27&amp;col=8&amp;locname=%E5%8C%97%E4%BA%AC&amp;locid=0" target="_blank"><img src="https://t12.baidu.com/it/u=609189299,2475140924&amp;fm=173&amp;app=25&amp;f=JPEG?w=218&amp;h=146&amp;s=FC0EAF5719C9494114F4807B0300807A"></a>          <a class="item-title" href="http://finance.ifeng.com/a/20180925/16522184_0.shtml" mon="" target="_blank">华润昆仑域质量门后发&ldquo;家书&rdquo; 业主:这是对我们...</a>
        |                  </div>
        |                </div>
        |              </div>
        |            </div>
        |          </div>
        |        </div>
        |        <div alog-group="log-local-right" class="l-right-col">
        |          <div class="mod tbox" id="internet-aside-gsdt">
        |            <div class="hd line">
        |              <h3>新闻资讯</h3>
        |            </div>
        |            <div class="bd" id="localnews-zixun">
        |              <ul class="ulist">
        |                <li>
        |                  <a href="http://auto.ifeng.com/changshangxinwen/20180925/1239174.shtml" mon="c=civilnews&amp;ct=0&amp;a=27&amp;col=8&amp;locname=%E5%8C%97%E4%BA%AC&amp;locid=0" target="_blank">2018北京现代汽车金融&ldquo;我是跑者&rdquo;北京站收官</a>
        |                </li>
        |                <li>
        |                  <a href="http://www.cnr.cn/bj/jrbj/20180925/t20180925_524369076.shtml" mon="c=civilnews&amp;ct=0&amp;a=27&amp;col=8&amp;locname=%E5%8C%97%E4%BA%AC&amp;locid=0" target="_blank">北京开往香港首趟高铁满员启程 全程8小时58分钟</a>
        |                </li>
        |                <li>
        |                  <a href="http://auto.ifeng.com/quanmeiti/20180925/1239251.shtml" mon="c=civilnews&amp;ct=0&amp;a=27&amp;col=8&amp;locname=%E5%8C%97%E4%BA%AC&amp;locid=0" target="_blank">三人可成合乘 北京南站试点网约公交车</a>
        |                </li>
        |                <li>
        |                  <a href="http://finance.people.com.cn/n1/2018/0925/c1004-30311455.html" mon="c=civilnews&amp;ct=0&amp;a=27&amp;col=8&amp;locname=%E5%8C%97%E4%BA%AC&amp;locid=0" target="_blank">北京南站启用全国首批&ldquo;实名制快速验证闸机通道&rdquo;</a>
        |                </li>
        |                <li>
        |                  <a href="http://sports.sohu.com/20180925/n550481035.shtml" mon="c=civilnews&amp;ct=0&amp;a=27&amp;col=8&amp;locname=%E5%8C%97%E4%BA%AC&amp;locid=0" target="_blank">&ldquo;一起动&rdquo;跑者声音：北马后区跑者国歌听不完整</a>
        |                </li>
        |                <li>
        |                  <a href="http://sports.sohu.com/20180925/n550501682.shtml" mon="c=civilnews&amp;ct=0&amp;a=27&amp;col=8&amp;locname=%E5%8C%97%E4%BA%AC&amp;locid=0" target="_blank">UFC二度来华：北京站的一片叫好声，可以换来持...</a>
        |                </li>
        |                <li>
        |                  <a href="http://news.ynet.com/2018/09/25/1439574t70.html" mon="c=civilnews&amp;ct=0&amp;a=27&amp;col=8&amp;locname=%E5%8C%97%E4%BA%AC&amp;locid=0" target="_blank">形成产业集群 北京房山的葡萄酒已远销法国</a>
        |                </li>
        |              </ul>
        |            </div>
        |          </div>
        |        </div>
        |        <div class="ad-banner" id="localNews_ad"></div>
        |        <div class="city_view" id="city_view">
        |          <div class="city_list"></div>
        |          <div class="btn_back" id="btn_back">      返回     </div>
        |          <div class="btn_close" id="btn_close"></div>
        |          <p class="city-tip">您所选城市新闻不足，将展示省会新闻</p>
        |          <div class="up_triangle"></div>
        |        </div>
        |        <div class="loading" id="status" style="display: none;">     正在加载，请稍候...    </div>
        |      </div>
        |      <ul class="mod-sidebar" id="goTop">
        |        <li class="item report button-rotate" data-text="举报">
        |          <a href="http://report.12377.cn:13225/toreportinputNormal_anis.do">举报</a>
        |        </li>
        |        <li class="item qr-code button-rotate" data-text="二维码">
        |          <a href="javascript:void(0);">二维码</a>
        |        </li>
        |        <li class="qr-code-container clearfix">
        |          <span class="item-container left"> <span class="img-container"> <img src="//gss0.bdstatic.com/5foIcy0a2gI2n2jgoY3K/static/fisp_static/common/img/sidebar/1014720a_d31158d.png"> </span> </span> <span class="item-container right">
        |            <p class="title">百度新闻客户端</p>
        |            <ul>
        |              <li>扫描二维码下载</li>
        |              <li>随时随地收看更多新闻</li>
        |            </ul>
        |          </span>
        |        </li>
        |        <li class="item favorite button-rotate" data-text="收藏本站">
        |          <a href="javascript:void(0);">收藏本站</a>
        |        </li>
        |        <li class="item search button-rotate" data-text="搜索">
        |          <a href="javascript:void(0);" id="search-btn">搜索</a>
        |        </li>
        |        <li class="item feedback button-rotate" data-text="用户反馈" id="feedbackbtn">
        |          <a href="javascript:void(0);">用户反馈</a>
        |        </li>
        |        <li class="item gotop">
        |          <a href="javascript:void(0);" id="gotop_btn" onclick="window.scroll(0, 0)"></a>
        |        </li>
        |        <li class="searchbox">
        |          <span class="close-btn"></span>
        |          <p>
        |            <input id="tn" name="tn" type="hidden" value="news"> <input id="from" name="from" type="hidden" value="news"> <input id="cl" name="cl" type="hidden" value="2"> <input id="rn" name="rn" type="hidden" value="20"> <input id="ct" name="ct" type="hidden" value="1"> <input autocomplete="off" class="searchInput" maxlength="100" name="word" tabindex="1" type="text" value="输入搜索词"> <button class="submit-btn" type="button">搜索</button>
        |          </p>
        |        </li>
        |        <li class="close-tip">收起<i class="arrow"></i>
        |        </li>
        |      </ul>
        |      <style>#goTop{    position: fixed;    width: 54px;    left: 50%;    margin-left: 502px;    bottom: 20px;    _position: absolute;    _top: expression(eval(document.documentElement.scrollTop || document.body.scrollTop)+eval(document.documentElement.clientHeight || document.body.clientHeight)-361+'px');    z-index:998;}</style>
        |    </div>
        |    <div id="footerwrapper">
        |      <div alog-alias="hunter-start-bottombar" alog-group="log-footer-bottombar" class="bottombar">
        |        <div class="bottombar-inner clearfix">
        |          <div class="bot-left">
        |            <div class="title-container">
        |              <i class="icon">&amp;#xa0;</i>
        |              <h4>更多精彩内容</h4>
        |            </div>
        |            <div class="qrcode-container clearfix">
        |              <div class="img-container">
        |                <img src="//gss0.bdstatic.com/5foIcy0a2gI2n2jgoY3K/static/fisp_static/common/img/footer/1014720b_45d192d.png">        </div>
        |              <div class="link-container">
        |                <a href="http://downpack.baidu.com/baidunews_AndroidPhone_1014720b.apk" target="_blank">Android版下载</a>         <a href="https://itunes.apple.com/cn/app/id482820737" target="_blank">iPhone版下载</a>
        |              </div>
        |              <p class="info">扫描二维码, 收看更多新闻</p>
        |            </div>
        |          </div>
        |          <div class="bot-center">
        |            <div class="title-container">
        |              <i class="icon">&amp;#xa0;</i>
        |              <h4>相关功能</h4>
        |            </div>
        |            <ul class="item-container clearfix">
        |              <li class="item item-03">
        |                <a href="http://newsalert.baidu.com/na?cmd=0" target="_blank">邮件新闻订阅</a>
        |              </li>
        |              <li class="item item-06">
        |                <a href="//news.baidu.com/z/resource/pc/staticpage/newscode.html" target="_blank">新闻免费代码</a>
        |              </li>
        |            </ul>
        |          </div>
        |          <div class="bot-right">
        |            <div class="title-container">
        |              <i class="icon">&amp;#xa0;</i>
        |              <h4>百度新闻独家出品</h4>
        |            </div>
        |            <ol>
        |              <li>1. 新闻由机器选取每5分钟自动更新</li>
        |              <li>2. 百度新闻搜索源于互联网新闻网站和频道，系统自动分类排序</li>
        |              <li>3. 百度不刊登或转载任何完整的新闻内容</li>
        |            </ol>
        |          </div>
        |        </div>
        |      </div>
        |      <div style="font-size:12px;text-align:center;">     责任编辑：胡彦BN098 刘石娟BN068 谢建BN085 李芳雨BN091 储信艳BN087 焦碧碧BN084 禤聪BN095 王鑫BN060 崔超BN071 违法和不良信息举报电话：010-59922128   </div>
        |      <div alog-alias="hunter-start-footer" alog-group="log-footer" id="footer">
        |        <a href="https://news.baidu.com/z/resource/wap/protocol/baidu_news_protocol.html">用户协议</a>     <a href="https://www.baidu.com/duty/wise/wise_secretright.html">隐私策略</a>     <a href="//help.baidu.com/newadd?prod_id=5&amp;category=1">投诉中心</a>     <span>京公网安备11000002000001号</span>     <a href="//news.baidu.com/licence.html">互联网新闻信息服务许可</a>     <span>&copy;2018Baidu</span>     <a class="cy" href="//www.baidu.com/duty/">使用百度前必读</a>     <a class="img-link img-link1" href="http://net.china.cn/chinese/index.htm" target="_blank"> </a>     <a class="img-link img-link2" href="http://www.cyberpolice.cn/wfjb/" target="_blank"> </a>     <a class="img-link img-link3" href="http://www.bjjubao.org/" target="_blank"> </a>
        |      </div>
        |    </div>
        |    <style>.focustop-anchor{    height:0;    line-height:0;    font-size:0;}#headerwrapper{    width:100%;}</style>
        |  </body>
        |</html>
      """.stripMargin

    val domHtml=XPathUtil.toDocument(xmlHtml)

    println(XPathUtil.getNodeListFromXML(domHtml, "//a"))
  }

  test("json to tree"){
    val json=Source.fromFile("src/test/scala/com/testerhome/appcrawler/ut/source.json").mkString
    log.info(json)
    val m=TData.from(json)
    log.info(m)

    val content=TData.toHtml(m)
    log.info("content")
    log.info(content)

    val dom=XPathUtil.toDocument(content)

    println(XPathUtil.getNodeListByXPath("//a", dom))

  }


  test("xml"){
    val xml=Source.fromFile("/tmp/4").mkString
    XPathUtil.toDocument(xml)
  }

  test("getAttributesFromNode"){
    val nodes=XPathUtil.getNodeListFromXML(
      XPathUtil.toDocument(
        Source.fromFile("src/test/scala/com/testerhome/appcrawler/ut/html.xml").mkString
      ), "//a"
    ).asInstanceOf[NodeList]
    0 until nodes.getLength foreach(i=>{
      val attributes=XPathUtil.getAttributesFromNode(nodes.item(i))
      log.info(attributes)
      XPathUtil.xpathExpr=List("class", "name", "id", "tag", "innerText")
      log.info(XPathUtil.xpathExpr)
      val xpath=XPathUtil.getXPathFromAttributes(attributes)
      log.info(xpath)

    })

  }









}
