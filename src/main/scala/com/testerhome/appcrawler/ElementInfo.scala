package com.testerhome.appcrawler

case class ElementInfo(
                        var reqDom: String = "",
                        var resDom: String = "",
                        var reqHash: String = "",
                        var resHash: String = "",
                        var reqImg:String="",
                        var resImg:String="",
                        var clickedIndex: Int = -1,
                        var action: ElementStatus.Value = ElementStatus.Ready,
                        var element: URIElement = URIElement(url="Init", tag="", id="", name="", xpath="")
                      )