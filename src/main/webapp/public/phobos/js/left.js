var ready = function () {
    Sidebar.initialize(), UI.smart_selects(), $("[data-toggle='tooltip']").tooltip(), window.devicePixelRatio >= 1.2 && $("[data-2x]").each(function () {
        "IMG" == this.tagName ? $(this).attr("src", $(this).attr("data-2x")) : $(this).css({
            "background-image": "url(" + $(this).attr("data-2x") + ")"
        })
    })
};



var UI = {
    smart_selects: function () {
        var t = $("[data-smart-select]");
        $.each(t, function (t, e) {
            var n = $(e);
            if (n.parent().hasClass("fake-select-wrap"))
                return void n.siblings(".fake-select").html(n.find("option:selected").text());
            var i = $("<div class='fake-select-wrap' />"),
                    s = $("<div class='fake-select'></div>");
            n.wrap(i), n.after(s), s.html(n.find("option:selected").text()), n.change(function () {
                s.html($(this).find("option:selected").text())
            }), n.focus(function () {
                s.addClass("focus")
            }).focusout(function () {
                s.removeClass("focus")
            })
        })
    }
},
Sidebar = {
    position: function (str, m, i) {
        return str.split(m, i).join(m).length;
    },
    initialize: function () {
        var t = $(".main-sidebar"),
                e = t.find(".current-user .menu");
        $(".current-user .name").click(function (t) {
            t.preventDefault(), t.stopPropagation(), e.toggleClass("active")
        }), e.click(function (t) {
            t.stopPropagation()
        }), $("body").click(function () {
            e.removeClass("active")
        });
        var n = t.find("[data-toggle~='sidebar']");
        n.click(function (t) {
            if (t.preventDefault(), !utils.isTablet()) {
                $(this).closest(".submenu").length || n.not(this).removeClass("toggled").siblings(".submenu").slideUp(300, o);
                var e = $(this),
                        i = $(this).siblings(".submenu");
                e.toggleClass("toggled"), e.hasClass("toggled") ? i.slideDown(300, o) : i.slideUp(300, o)
            }
        });
        var i = window.location.pathname;
        var u = i.substr(0, Sidebar.position(i, '/', 3));

        t.find(".menu-section a").removeClass("active");
        var s = t.find("a[href='" + u + "']");

        if (s.length) {
            if (s.addClass("active"), s.parents(".submenu").length) {
                var r = s.closest(".option").find("[data-toggle~='sidebar']");
                r.addClass("active toggled"), s.parents(".submenu").addClass("active")
            }
        } else
            t.find(".menu-section .option > a:eq(0)").addClass("active");
        
        var o = function () {
            var t = $("body").height();
            $(".main-sidebar").css("bottom", "auto");
            var e = $(".main-sidebar").height();
            t > e ? $(".main-sidebar").css("bottom", 0) : $(".main-sidebar").css("bottom", "auto")
        },
                a = $("#content .sidebar-toggler");
        a.click(function (t) {
            t.stopPropagation(), $("body").toggleClass("open-sidebar")
        }), $("#content").click(function () {
            $("body").removeClass("open-sidebar")
        });


        /*var lef = $(".left-bar"), i = url_server(window.location.pathname);
         lef.find(".list-group a").removeClass("act");
         var item = lef.find("a[href='" + i + "']");
         if (item.length) {
         item.addClass("act")
         } else {
         lef.find(".left-group .list-group-item > a:eq(0)").addClass("act");
         }*/
    }
};




window.utils = {
    isFirefox: function () {
        return navigator.userAgent.toLowerCase().indexOf("firefox") > -1
    },
    animation_ends: function () {
        return "animationend webkitAnimationEnd oAnimationEnd"
    },
    isTablet: function () {
        return $(".main-sidebar").width() < 100
    },
    get_timestamp: function (t) {
        return moment().subtract("days", t).toDate().getTime()
    }
},
function () {
    var t = function () {
        $("body#sidebar").length && ($("html, body").css("height", "100%"), $(".main-sidebar").wrapInner("<div class='scroll-wrapper'></div>"))
    };
    $(document).on("ready page:load", t)
}();



$(document).on("ready page:load", ready), $(document).on("page:change", function () {
    window.prevPageYOffset = window.pageYOffset, window.prevPageXOffset = window.pageXOffset
}), $(document).on("page:load", function () {
    $(".fix-scroll").length > 0 && $(".fix-scroll").hide().show()
});
