import org.scalatest.{Matchers, FunSuite}

/**
  * Created by seveniruby on 16/3/26.
  */
class TestRichData extends FunSuite with Matchers{


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
    val node=RichData.parseXPath("//*[@resource-id='com.xueqiu.android:id/action_search']", dom)(0)
    println(node)
    node("resource-id") should be equals("com.xueqiu.android:id/action_search")
    node("content-desc") should be equals("输入股票名称/代码")
  }
  test("extra attribute from xpath"){
    val node=RichData.parseXPath("//*[@resource-id='com.xueqiu.android:id/action_search']/@resource-id", dom)(0)
    println(node)
    node.values.toList(0) should be equals("com.xueqiu.android:id/action_search")

    //todo:暂不支持
    val value=RichData.parseXPath("string(//*[@resource-id='com.xueqiu.android:id/action_search']/@resource-id)", dom)
    println(value)

  }

  test("get parent path"){
    val value=RichData.parseXPath("//*[@resource-id='com.xueqiu.android:id/action_search']/parent::*", dom)
    value.foreach(println)
    println(value)

    val ancestor=RichData.parseXPath("//*[@resource-id='com.xueqiu.android:id/action_search']/ancestor-or-self::*", dom)
    ancestor.foreach(x=>if(x.contains("tag")) println(x("tag")))
    println(ancestor)
    ancestor.foreach(println)

    val ancestorName=RichData.parseXPath("//*[@resource-id='com.xueqiu.android:id/action_search']/ancestor::name", dom)
    ancestorName.foreach(println)
  }

}
