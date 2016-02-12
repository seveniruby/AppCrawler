
import org.scalatest.FunSuite

import scala.collection.mutable
import scala.collection.mutable.{Map, ListBuffer}
import scala.io.Source

/**
  * Created by seveniruby on 15/11/28.
  */
class TestTraversal extends FunSuite{


  test("isBlack"){
    val appium=new Crawler
    appium.black("stock_item.*")
    val m1=mutable.Map("name"->"stock_item_1", "value"->"")
    val m2=mutable.Map("value"->"stock_item_1", "name"->"")
    val m3=mutable.Map("name"->"stock_item_1", "value"->"stock_item_1")
    val m4=mutable.Map("name"->"", "value"->"")
    val m5=mutable.Map("name"->"ss", "value"->"dd")
    assert(true==appium.isBlack(m1))
    assert(true==appium.isBlack(m2))
    assert(true==appium.isBlack(m3))
    assert(false==appium.isBlack(m4))
    assert(false==appium.isBlack(m5))

  }


  test("getAllElements"){
    val xml=
      """
        |<AppiumAUT>
        |    <UIAApplication height="480" width="320" y="0" x="0" path="/0" hint="" visible="true" valid="true" enabled="true" dom="" value="" label="雪球" name="雪球">
        |        <UIAWindow height="480" width="320" y="0" x="0" path="/0/0" hint="" visible="true" valid="true" enabled="true" dom="" value="" label="" name="">
        |            <UIANavigationBar height="44" width="320" y="20" x="0" path="/0/0/0" hint="" visible="true" valid="true" enabled="true" dom="" value="" label="" name="SNBHotEventVC">
        |                <UIAImage height="64" width="320" y="0" x="0" path="/0/0/0/0" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="" name="">
        |                    <UIAImage height="0" width="320" y="64" x="0" path="/0/0/0/0/0" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="" name="">
        |                    </UIAImage>
        |                </UIAImage>
        |                <UIAButton height="32" width="30" y="26" x="16" path="/0/0/0/1" hint="" visible="true" valid="true" enabled="true" dom="" value="" label="nav_icon_back" name="nav_icon_back">
        |                </UIAButton>
        |                <UIAStaticText height="20" width="160" y="62" x="80" path="/0/0/0/2" hint="" visible="true" valid="true" enabled="true" dom="" value="#券商板块#" label="#券商板块#" name="#券商板块#">
        |                </UIAStaticText>
        |                <UIAButton height="21" width="21" y="31.5" x="8" path="/0/0/0/3" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="Back" name="Back">
        |                </UIAButton>
        |            </UIANavigationBar>
        |            <UIAImage height="300" width="320" y="0" x="0" path="/0/0/1" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="" name="home_background_3">
        |            </UIAImage>
        |            <UIAImage height="480" width="320" y="0" x="0" path="/0/0/2" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="" name="">
        |            </UIAImage>
        |            <UIATableView height="366" width="320" y="64" x="0" path="/0/0/3" hint="" visible="true" valid="true" enabled="true" dom="" value="rows 1 to 2 of 17" label="" name="">
        |                <UIAStaticText height="24" width="102.5" y="79" x="109" path="/0/0/3/0" hint="" visible="true" valid="true" enabled="true" dom="" value="#券商板块#" label="#券商板块#" name="#券商板块#">
        |                </UIAStaticText>
        |                <UIATableCell height="62.6199951171875" width="320" y="140" x="0" path="/0/0/3/1" hint="" visible="true" valid="true" enabled="true" dom="" value="" label="" name="券商板块崛起，东兴证券一度涨停。消息面，国务院拟提请全国人大授权实施股票发行注册制。">
        |                    <UIAStaticText height="38" width="296" y="152" x="12" path="/0/0/3/1/0" hint="" visible="true" valid="true" enabled="true" dom="" value="券商板块崛起，东兴证券一度涨停。消息面，国务院拟提请全国人大授权实施股票发行注册制。" label="券商板块崛起，东兴证券一度涨停。消息面，国务院拟提请全国人大授权实施股票发行注册制。" name="券商板块崛起，东兴证券一度涨停。消息面，国务院拟提请全国人大授权实施股票发行注册制。">
        |                    </UIAStaticText>
        |                </UIATableCell>
        |                <UIATableGroup height="44" width="320" y="202.6199951171875" x="0" path="/0/0/3/2" hint="" visible="true" valid="true" enabled="true" dom="" value="" label="" name="">
        |                </UIATableGroup>
        |                <UIATableCell height="329" width="320" y="246.6199951171875" x="0" path="/0/0/3/3" hint="" visible="true" valid="true" enabled="true" dom="" value="" label="" name="12">
        |                    <UIAButton height="18" width="106.5" y="547.6199951171875" x="0" path="/0/0/3/3/0" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="12" name="12">
        |                    </UIAButton>
        |                    <UIAButton height="18" width="107" y="547.6199951171875" x="106.5" path="/0/0/3/3/1" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="22" name="22">
        |                    </UIAButton>
        |                    <UIAButton height="18" width="106.5" y="547.6199951171875" x="213.5" path="/0/0/3/3/2" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="赞助" name="赞助">
        |                    </UIAButton>
        |                </UIATableCell>
        |                <UIATableCell height="329" width="320" y="575.6199951171875" x="0" path="/0/0/3/4" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="" name="赞助">
        |                    <UIAButton height="18" width="106.5" y="0" x="0" path="/0/0/3/4/0" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="赞助" name="赞助">
        |                    </UIAButton>
        |                    <UIAButton height="18" width="107" y="0" x="0" path="/0/0/3/4/1" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="20" name="20">
        |                    </UIAButton>
        |                    <UIAButton height="18" width="106.5" y="0" x="0" path="/0/0/3/4/2" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="2" name="2">
        |                    </UIAButton>
        |                </UIATableCell>
        |                <UIATableCell height="401" width="320" y="904.6199951171875" x="0" path="/0/0/3/5" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="" name="赞助">
        |                    <UIAButton height="18" width="106.5" y="0" x="0" path="/0/0/3/5/0" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="赞助" name="赞助">
        |                    </UIAButton>
        |                    <UIAButton height="18" width="107" y="0" x="0" path="/0/0/3/5/1" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="15" name="15">
        |                    </UIAButton>
        |                    <UIAButton height="18" width="106.5" y="0" x="0" path="/0/0/3/5/2" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="2" name="2">
        |                    </UIAButton>
        |                </UIATableCell>
        |                <UIATableCell height="310" width="320" y="1305.6199951171875" x="0" path="/0/0/3/6" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="" name="赞助">
        |                    <UIAButton height="18" width="106.5" y="0" x="0" path="/0/0/3/6/0" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="赞助" name="赞助">
        |                    </UIAButton>
        |                    <UIAButton height="18" width="107" y="0" x="0" path="/0/0/3/6/1" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="4" name="4">
        |                    </UIAButton>
        |                    <UIAButton height="18" width="106.5" y="0" x="0" path="/0/0/3/6/2" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="转发" name="转发">
        |                    </UIAButton>
        |                </UIATableCell>
        |                <UIATableCell height="310" width="320" y="1615.6199951171875" x="0" path="/0/0/3/7" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="" name="赞助">
        |                    <UIAButton height="18" width="106.5" y="0" x="0" path="/0/0/3/7/0" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="赞助" name="赞助">
        |                    </UIAButton>
        |                    <UIAButton height="18" width="107" y="0" x="0" path="/0/0/3/7/1" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="2" name="2">
        |                    </UIAButton>
        |                    <UIAButton height="18" width="106.5" y="0" x="0" path="/0/0/3/7/2" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="7" name="7">
        |                    </UIAButton>
        |                </UIATableCell>
        |                <UIATableCell height="173.0001220703125" width="320" y="1925.6199951171875" x="0" path="/0/0/3/8" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="" name="赞助">
        |                    <UIAButton height="18" width="106.5" y="0" x="0" path="/0/0/3/8/0" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="赞助" name="赞助">
        |                    </UIAButton>
        |                    <UIAButton height="18" width="107" y="0" x="0" path="/0/0/3/8/1" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="2" name="2">
        |                    </UIAButton>
        |                    <UIAButton height="18" width="106.5" y="0" x="0" path="/0/0/3/8/2" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="1" name="1">
        |                    </UIAButton>
        |                </UIATableCell>
        |                <UIATableCell height="310" width="320" y="2098.6201171875" x="0" path="/0/0/3/9" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="" name="赞助">
        |                    <UIAButton height="18" width="106.5" y="0" x="0" path="/0/0/3/9/0" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="赞助" name="赞助">
        |                    </UIAButton>
        |                    <UIAButton height="18" width="107" y="0" x="0" path="/0/0/3/9/1" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="2" name="2">
        |                    </UIAButton>
        |                    <UIAButton height="18" width="106.5" y="0" x="0" path="/0/0/3/9/2" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="转发" name="转发">
        |                    </UIAButton>
        |                </UIATableCell>
        |                <UIATableCell height="310" width="320" y="2408.6201171875" x="0" path="/0/0/3/10" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="" name="赞助">
        |                    <UIAButton height="18" width="106.5" y="0" x="0" path="/0/0/3/10/0" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="赞助" name="赞助">
        |                    </UIAButton>
        |                    <UIAButton height="18" width="107" y="0" x="0" path="/0/0/3/10/1" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="1" name="1">
        |                    </UIAButton>
        |                    <UIAButton height="18" width="106.5" y="0" x="0" path="/0/0/3/10/2" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="转发" name="转发">
        |                    </UIAButton>
        |                </UIATableCell>
        |                <UIATableCell height="283" width="320" y="2718.6201171875" x="0" path="/0/0/3/11" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="" name="赞助">
        |                    <UIAButton height="18" width="106.5" y="0" x="0" path="/0/0/3/11/0" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="赞助" name="赞助">
        |                    </UIAButton>
        |                    <UIAButton height="18" width="107" y="0" x="0" path="/0/0/3/11/1" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="1" name="1">
        |                    </UIAButton>
        |                    <UIAButton height="18" width="106.5" y="0" x="0" path="/0/0/3/11/2" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="转发" name="转发">
        |                    </UIAButton>
        |                </UIATableCell>
        |                <UIATableCell height="329" width="320" y="3001.6201171875" x="0" path="/0/0/3/12" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="" name="赞助">
        |                    <UIAButton height="18" width="106.5" y="0" x="0" path="/0/0/3/12/0" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="赞助" name="赞助">
        |                    </UIAButton>
        |                    <UIAButton height="18" width="107" y="0" x="0" path="/0/0/3/12/1" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="1" name="1">
        |                    </UIAButton>
        |                    <UIAButton height="18" width="106.5" y="0" x="0" path="/0/0/3/12/2" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="3" name="3">
        |                    </UIAButton>
        |                </UIATableCell>
        |                <UIATableCell height="374" width="320" y="3330.6201171875" x="0" path="/0/0/3/13" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="" name="赞助">
        |                    <UIAButton height="18" width="106.5" y="0" x="0" path="/0/0/3/13/0" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="赞助" name="赞助">
        |                    </UIAButton>
        |                    <UIAButton height="18" width="107" y="0" x="0" path="/0/0/3/13/1" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="评论" name="评论">
        |                    </UIAButton>
        |                    <UIAButton height="18" width="106.5" y="0" x="0" path="/0/0/3/13/2" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="转发" name="转发">
        |                    </UIAButton>
        |                </UIATableCell>
        |                <UIATableCell height="283" width="320" y="3704.6201171875" x="0" path="/0/0/3/14" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="" name="赞助">
        |                    <UIAButton height="18" width="106.5" y="0" x="0" path="/0/0/3/14/0" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="赞助" name="赞助">
        |                    </UIAButton>
        |                    <UIAButton height="18" width="107" y="0" x="0" path="/0/0/3/14/1" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="评论" name="评论">
        |                    </UIAButton>
        |                    <UIAButton height="18" width="106.5" y="0" x="0" path="/0/0/3/14/2" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="转发" name="转发">
        |                    </UIAButton>
        |                </UIATableCell>
        |                <UIATableCell height="396" width="320" y="3987.6201171875" x="0" path="/0/0/3/15" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="" name="赞助">
        |                    <UIAButton height="18" width="106.5" y="0" x="0" path="/0/0/3/15/0" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="赞助" name="赞助">
        |                    </UIAButton>
        |                    <UIAButton height="18" width="107" y="0" x="0" path="/0/0/3/15/1" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="评论" name="评论">
        |                    </UIAButton>
        |                    <UIAButton height="18" width="106.5" y="0" x="0" path="/0/0/3/15/2" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="转发" name="转发">
        |                    </UIAButton>
        |                </UIATableCell>
        |                <UIATableCell height="173" width="320" y="4383.6201171875" x="0" path="/0/0/3/16" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="" name="赞助">
        |                    <UIAButton height="18" width="106.5" y="0" x="0" path="/0/0/3/16/0" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="赞助" name="赞助">
        |                    </UIAButton>
        |                    <UIAButton height="18" width="107" y="0" x="0" path="/0/0/3/16/1" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="评论" name="评论">
        |                    </UIAButton>
        |                    <UIAButton height="18" width="106.5" y="0" x="0" path="/0/0/3/16/2" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="转发" name="转发">
        |                    </UIAButton>
        |                </UIATableCell>
        |                <UIATableCell height="129" width="320" y="4556.6201171875" x="0" path="/0/0/3/17" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="" name="赞助">
        |                    <UIAButton height="18" width="106.5" y="0" x="0" path="/0/0/3/17/0" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="赞助" name="赞助">
        |                    </UIAButton>
        |                    <UIAButton height="18" width="107" y="0" x="0" path="/0/0/3/17/1" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="评论" name="评论">
        |                    </UIAButton>
        |                    <UIAButton height="18" width="106.5" y="0" x="0" path="/0/0/3/17/2" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="转发" name="转发">
        |                    </UIAButton>
        |                </UIATableCell>
        |                <UIATableCell height="44" width="320" y="4685.6201171875" x="0" path="/0/0/3/18" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="" name="更多">
        |                    <UIAStaticText height="47" width="320" y="0" x="0" path="/0/0/3/18/0" hint="" visible="false" valid="true" enabled="true" dom="" value="更多" label="更多" name="更多">
        |                    </UIAStaticText>
        |                </UIATableCell>
        |            </UIATableView>
        |            <UIAButton height="40" width="40" y="430" x="0" path="/0/0/4" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="toolbar icon keyboard down" name="toolbar icon keyboard down">
        |                <UIAImage height="40" width="40" y="430" x="0" path="/0/0/4/0" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="" name="toolbar_icon_keyboard_down">
        |                </UIAImage>
        |            </UIAButton>
        |            <UIATextView height="36" width="262" y="437" x="12" path="/0/0/5" hint="" visible="true" valid="true" enabled="true" dom="" value="#券商板块#" label="聊天输入框" name="聊天输入框">
        |            </UIATextView>
        |            <UIAButton height="0" width="0" y="437" x="276" path="/0/0/6" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="toolbar icon add" name="toolbar icon add">
        |                <UIAImage height="0" width="0" y="437" x="276" path="/0/0/6/0" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="" name="toolbar_icon_add">
        |                </UIAImage>
        |            </UIAButton>
        |            <UIAButton height="35" width="35" y="437" x="278" path="/0/0/7" hint="" visible="true" valid="true" enabled="true" dom="" value="" label="toolbar icon face" name="toolbar icon face">
        |            </UIAButton>
        |            <UIATabBar height="49" width="320" y="431" x="0" path="/0/0/8" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="" name="">
        |                <UIAImage height="0.5" width="320" y="430.5" x="0" path="/0/0/8/0" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="" name="">
        |                </UIAImage>
        |                <UIAImage height="49" width="320" y="431" x="0" path="/0/0/8/1" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="" name="">
        |                </UIAImage>
        |                <UIAButton height="48" width="60" y="432" x="2" path="/0/0/8/2" hint="" visible="false" valid="true" enabled="true" dom="" value="1" label="首页" name="首页">
        |                </UIAButton>
        |                <UIAButton height="48" width="60" y="432" x="66" path="/0/0/8/3" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="自选" name="自选">
        |                </UIAButton>
        |                <UIAButton height="48" width="60" y="432" x="130" path="/0/0/8/4" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="动态" name="动态">
        |                </UIAButton>
        |                <UIAStaticText height="18" width="30" y="434" x="166" path="/0/0/8/5" hint="" visible="false" valid="true" enabled="true" dom="" value="397" label="397" name="397">
        |                </UIAStaticText>
        |                <UIAButton height="48" width="60" y="432" x="194" path="/0/0/8/6" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="组合" name="组合">
        |                </UIAButton>
        |                <UIAButton height="48" width="60" y="432" x="258" path="/0/0/8/7" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="交易" name="交易">
        |                </UIAButton>
        |            </UIATabBar>
        |            <UIAScrollView height="216" width="320" y="480" x="0" path="/0/0/9" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="" name="">
        |                <UIAButton height="40" width="40" y="491" x="8" path="/0/0/9/0" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="smile" name="smile">
        |                </UIAButton>
        |                <UIAButton height="40" width="40" y="491" x="52" path="/0/0/9/1" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="lol" name="lol">
        |                </UIAButton>
        |                <UIAButton height="40" width="40" y="491" x="96" path="/0/0/9/2" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="guzhang" name="guzhang">
        |                </UIAButton>
        |                <UIAButton height="40" width="40" y="491" x="140" path="/0/0/9/3" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="qiaopi" name="qiaopi">
        |                </UIAButton>
        |                <UIAButton height="40" width="40" y="491" x="184" path="/0/0/9/4" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="jiayou" name="jiayou">
        |                </UIAButton>
        |                <UIAButton height="40" width="40" y="491" x="228" path="/0/0/9/5" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="earn" name="earn">
        |                </UIAButton>
        |                <UIAButton height="40" width="40" y="491" x="272" path="/0/0/9/6" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="niubi" name="niubi">
        |                </UIAButton>
        |                <UIAButton height="40" width="40" y="536" x="8" path="/0/0/9/7" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="angry" name="angry">
        |                </UIAButton>
        |                <UIAButton height="40" width="40" y="536" x="52" path="/0/0/9/8" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="weep" name="weep">
        |                </UIAButton>
        |                <UIAButton height="40" width="40" y="536" x="96" path="/0/0/9/9" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="lose" name="lose">
        |                </UIAButton>
        |                <UIAButton height="40" width="40" y="536" x="140" path="/0/0/9/10" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="sleepy" name="sleepy">
        |                </UIAButton>
        |                <UIAButton height="40" width="40" y="536" x="184" path="/0/0/9/11" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="shiwang" name="shiwang">
        |                </UIAButton>
        |                <UIAButton height="40" width="40" y="536" x="228" path="/0/0/9/12" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="han" name="han">
        |                </UIAButton>
        |                <UIAButton height="40" width="40" y="536" x="272" path="/0/0/9/13" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="why" name="why">
        |                </UIAButton>
        |                <UIAButton height="40" width="40" y="581" x="8" path="/0/0/9/14" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="guile" name="guile">
        |                </UIAButton>
        |                <UIAButton height="40" width="40" y="581" x="52" path="/0/0/9/15" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="hand" name="hand">
        |                </UIAButton>
        |                <UIAButton height="40" width="40" y="581" x="96" path="/0/0/9/16" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="buxie" name="buxie">
        |                </UIAButton>
        |                <UIAButton height="40" width="40" y="581" x="140" path="/0/0/9/17" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="xunka" name="xunka">
        |                </UIAButton>
        |                <UIAButton height="40" width="40" y="581" x="184" path="/0/0/9/18" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="chimian" name="chimian">
        |                </UIAButton>
        |                <UIAButton height="40" width="40" y="581" x="228" path="/0/0/9/19" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="gerou" name="gerou">
        |                </UIAButton>
        |                <UIAButton height="40" width="40" y="581" x="272" path="/0/0/9/20" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="haixiu" name="haixiu">
        |                </UIAButton>
        |                <UIAButton height="40" width="40" y="626" x="8" path="/0/0/9/21" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="shabi" name="shabi">
        |                </UIAButton>
        |                <UIAButton height="40" width="40" y="626" x="52" path="/0/0/9/22" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="tuxie" name="tuxie">
        |                </UIAButton>
        |                <UIAButton height="40" width="40" y="626" x="96" path="/0/0/9/23" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="koubi" name="koubi">
        |                </UIAButton>
        |                <UIAButton height="40" width="40" y="626" x="140" path="/0/0/9/24" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="maishen" name="maishen">
        |                </UIAButton>
        |                <UIAButton height="40" width="40" y="626" x="184" path="/0/0/9/25" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="cancel" name="cancel">
        |                </UIAButton>
        |                <UIAButton height="30" width="60" y="631" x="245.33331298828125" path="/0/0/9/26" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="发送" name="发送">
        |                </UIAButton>
        |            </UIAScrollView>
        |            <UIAPageIndicator height="21" width="320" y="666" x="0" path="/0/0/10" hint="" visible="false" valid="true" enabled="true" dom="" value="page 1 of 3" label="" name="">
        |            </UIAPageIndicator>
        |        </UIAWindow>
        |        <UIAWindow height="480" width="320" y="0" x="0" path="/0/1" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="" name="">
        |        </UIAWindow>
        |        <UIAWindow height="480" width="320" y="0" x="0" path="/0/2" hint="" visible="true" valid="true" enabled="true" dom="" value="" label="" name="">
        |            <UIAStatusBar height="20" width="320" y="0" x="0" path="/0/2/0" hint="" visible="true" valid="true" enabled="true" dom="" value="" label="" name="">
        |                <UIAElement height="20" width="39" y="0" x="6" path="/0/2/0/0" hint="Swipe down with three fingers to reveal the notification center., Swipe up with three fingers to reveal the control center, Double-tap to scroll to top" visible="true" valid="true" enabled="true" dom="" value="" label="" name="Swipe down with three fingers to reveal the notification center., Swipe up with three fingers to reveal the control center, Double-tap to scroll to top">
        |                </UIAElement>
        |                <UIAElement height="20" width="13" y="0" x="50" path="/0/2/0/1" hint="Swipe down with three fingers to reveal the notification center., Swipe up with three fingers to reveal the control center, Double-tap to scroll to top" visible="true" valid="true" enabled="true" dom="" value="SSID" label="3 of 3 Wi-Fi bars" name="3 of 3 Wi-Fi bars">
        |                </UIAElement>
        |                <UIAElement height="20" width="49" y="0" x="138" path="/0/2/0/2" hint="Swipe down with three fingers to reveal the notification center., Swipe up with three fingers to reveal the control center, Double-tap to scroll to top" visible="true" valid="true" enabled="true" dom="" value="" label="2:32 PM" name="2:32 PM">
        |                </UIAElement>
        |                <UIAElement height="20" width="25" y="0" x="290" path="/0/2/0/3" hint="Swipe down with three fingers to reveal the notification center., Swipe up with three fingers to reveal the control center, Double-tap to scroll to top" visible="true" valid="true" enabled="true" dom="" value="" label="-100% battery power" name="-100% battery power">
        |                </UIAElement>
        |            </UIAStatusBar>
        |        </UIAWindow>
        |        <UIAWindow height="480" width="320" y="0" x="0" path="/0/3" hint="" visible="false" valid="true" enabled="true" dom="" value="" label="" name="">
        |        </UIAWindow>
        |    </UIAApplication>
        |</AppiumAUT>
        |
      """.stripMargin

    val appium=new Crawler
    appium.pageSource=xml
    appium.parseXml(appium.pageSource)
    println(appium.getAllElements("//UIAWindow[1]//*[@visible='true' and @name!='']"))
    println(appium.getAllElements("//UIAWindow[1]//*[@visible='true' and @value!='']"))
  }

  test("whilespace"){
    "abc\000df\001ef\nfef中国s\088\\x00x00f".foreach(x=>{
      println(x)
      println(x.isWhitespace)
    })

  }

/*
  test("assert result"){
    assertResult("1", "expection=1"){
      2
    }

  }
*/

  test("save config"){
    val conf=new CrawlerConf
    conf.save("conf.json")
  }
/*
  test("load config"){
    var conf=new CrawlerConf
    conf.baseUrl="xxx"
    println(conf.baseUrl)
    conf=conf.loadByJson4s("conf.json").get
    println(conf.baseUrl)
  }
*/

  test("load config by jackson"){
    var conf=new CrawlerConf
    conf.baseUrl="xxx"
    println(conf.baseUrl)
    conf=conf.load("conf.json")
    println(conf.baseUrl)
  }


}
