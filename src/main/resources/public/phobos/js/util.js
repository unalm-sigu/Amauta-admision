$(function () {
    $('input').attr('autocomplete', 'off');

    $('.scrollable').scroll(function () {

        var limit = ($('#profileContainer').length === 1) ? 230 : 0;
        var nav = $('.subbar');

        if ($(this).scrollTop() > limit) {
            nav.addClass("f-nav");
        } else {
            nav.removeClass("f-nav");
        }
    });


    var alto = $('.main-sidebar').height() - 100;
    $('.contenedor-secciones').height(alto);

    $(window).resize(function () {
        var alto = $('.main-sidebar').height() - 100;
        $('.contenedor-secciones').height(alto);
    });

});

function notifyTop(message, type, position, title, icon) {
    this.notify(message, type, "topRight", title, icon);
}

iziToast.settings({
    timeout: 8000,
    resetOnHover: false,
    transitionIn: 'flipInX',
    transitionOut: 'flipOutX',
    displayMode: 2
});
// transitionIn: 'flipInX', 'fadeInUp','bounceInLeft', 'fadeIn', 'fadeInDown', 'fadeInLeft'

// bottomRight, bottomLeft, topRight, topLeft, topCenter, bottomCenter, center
function notify(message, type, position, title, icon) {
    type = type == null ? 'default' : type;
    title = title == null ? '' : title;
    var mType = this.MESSAGETYPE[type];
    icon = icon == null ? mType.icon : icon;
    message = message == null ? 'null' : message;
    position = (position == null || position == '') ? 'bottomRight' : position;
    setTimeout(function () {
        iziToast.show({
            title: title,
            message: message,
            icon: icon,
            color: mType.color,
            position: position
        });
    }, 500);
}

MESSAGETYPE = {
    success: {color: 'green', icon: 'fa fa-check'},
    info: {color: 'blue', icon: 'fa fa-info'},
    warning: {color: 'yellow', icon: 'fa fa-exclamation-triangle'},
    error: {color: 'red', icon: 'fa fa-ban'},
    default: {color: '', icon: 'fa fa-comment-alt'}
}

function notifyBootbox(message, type) {
    var t = (type == null) ? 'info' : type;
    var icons = '<i class="fa fa-info-circle fa-4x text-primary"></i>';
    var clazz = "btn-primary";
    switch (t) {
        case "error":
            clazz = "btn-danger";
            icons = '<i class="fa fa-exclamation-circle fa-4x text-danger"></i>';
            break;
        case "success":
            clazz = "btn-success";
            icons = '<i class="fa fa-check-circle fa-4x text-success"></i>';
            break;
        case "warning":
            clazz = "btn-warning";
            icons = '<i class="fa fa-exclamation-circle fa-4x text-warning"></i>';
            break;
    }

    var msg = '<table width="100%"><tr><td class="v-middle">';
    msg += icons;
    msg += '</td><td class="h4 v-middle"><div class="m-l">';
    msg += message;
    msg += '</div></td></tr></table>';

    setTimeout(function () {
        bootbox.alert({
            message: msg,
            buttons: {ok: {label: "Aceptar", className: clazz}}
        });
    }, 100);
}

function exitSession() {
    var win = window.open('http://www.google.com.mx/accounts/Logout2', '_blank', 'modal=yes,width=500,height=500');
    location.href = "/logout";
}

function randString(n) {
    if (!n) {
        n = 5;
    }

    var text = '';
    var possible = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    for (var i = 0; i < n; i++) {
        text += possible.charAt(Math.floor(Math.random() * possible.length));
    }

    return text;
}

$.fn.datepicker.defaults.format = "dd/mm/yyyy";
$.fn.datepicker.defaults.language = "es";
$.fn.datepicker.defaults.autoclose = true;
$.fn.datepicker.defaults.todayHighlight = false;
$.fn.datepicker.defaults.showButtonPanel = false;

Sidebar = {
    position: function (str, m, i) {
        return str.split(m, i).join(m).length;
    },
    initialize: function () {
        var t = $(".nav-primary"),
                i = window.location.pathname,
                u = i.substr(0, Sidebar.position(i, '/', 3));
        t.find(".nav li").removeClass("active");
        var s = t.find("a[href='" + u + "']").parent();
        if (s.length) {
            if (s.addClass("active"), s.parents(".nav").length) {
                s.parents(".nav").parent().addClass("active")
            }
        } else {
            t.find(".nav li:eq(0)").addClass("active");
        }
    }
};

//Sidebar.initialize();

Topbar = {
    position: function (str, m, i) {
        return str.split(m, i).join(m).length;
    },
    initialize: function () {
        var t = $(".nav-topbar"),
                i = window.location.pathname;

        var u = i.substr(0, Sidebar.position(i, '/', 4));
        if ((i.match(/\//g) || []).length > 5) {
            u = i.substr(0, Sidebar.position(i, '/', 5));
        }
        var s = t.find("a[href='" + u + "']").parent();

        if (s.length) {
            if (s.addClass("active"), s.parents(".nav").length) {
                s.parents("li").addClass("active");
            }
        } else {
            t.find(".nav li:eq(0)").addClass("active");
        }
    }
};

//Topbar.initialize();

MODAL = {
    idModalMd: "modalVik",
    idModalLg: "modalVikLarge",
    idModalSm: "modalVikSmall",
    idModalFoto: "modalVikFoto",
    modalActivo: null,
    idContent: "contentModalVik",
    idTitle: "titleModalVik",
    idSize: "modalVik-size",
    idBody: "bodyModalVik",
    idFooter: "footerModalVik",
    idBtnClose: "btnCerrarModalVik",
    idDivButtons: "buttonsModalVik",
    textButtonAffected: "",
    textModalWait: "",
    buttonAffected: null,
    init: function (type) {
        if (type == "sm") {
            MODAL.modalActivo = $("#" + MODAL.idModalSm);
        } else if (type == "lg") {
            MODAL.modalActivo = $("#" + MODAL.idModalLg);
        } else if (type == "md") {
            MODAL.modalActivo = $("#" + MODAL.idModalMd);
        } else if (type == "foto") {
            MODAL.modalActivo = $("#" + MODAL.idModalFoto);
        }
        MODAL.body("");
        MODAL.buttons("");
        MODAL.activateButtons();
    },
    size: function (size) {
        var divSize = MODAL.modalActivo.find("#" + MODAL.idSize);
        divSize.removeClass("modal-sm");
        divSize.removeClass("modal-lg");
        divSize.removeClass("modal-md");

        if (size == "sm") {
            divSize.addClass("modal-sm");
        } else if (size == "lg") {
            divSize.addClass("modal-lg");
        } else if (size == "md") {
            divSize.addClass("modal-md");
        }
    },
    modalDefault: function () {
        if (MODAL.modalActivo == null) {
            MODAL.modalActivo = $("#" + MODAL.idModalMd);
        }
    },
    title: function (html) {
        MODAL.modalDefault();
        var title = MODAL.modalActivo.find("#" + MODAL.idTitle);
        var div = title.closest("div");
        if (html != "") {
            div.removeClass("hide");
            title.html(html);
        } else {
            div.addClass("hide");
        }
    },
    buttons: function (html) {
        MODAL.modalDefault();
        if (html == null) {
            return MODAL.modalActivo.find("#" + MODAL.idDivButtons);
        }
        MODAL.modalActivo.find("#" + MODAL.idDivButtons).html(html);
    },
    body: function (html) {
        MODAL.modalDefault();
        if (html == null) {
            return MODAL.modalActivo.find("#" + MODAL.idBody);
        }
        MODAL.modalActivo.find("#" + MODAL.idBody).html(html);
    },
    getBody: function () {
        MODAL.modalDefault();
        return MODAL.modalActivo.find("#" + MODAL.idBody);
    },
    getFooter: function () {
        MODAL.modalDefault();
        return MODAL.modalActivo.find("#" + MODAL.idFooter);
    },
    show: function () {
        MODAL.modalDefault();
        MODAL.modalActivo.modal();
    },
    invisible: function () {
        MODAL.modalDefault();
        MODAL.modalActivo.modal("hide");
    },
    hide: function () {
        MODAL.modalDefault();
        MODAL.modalActivo.modal("hide");
        MODAL.limpiar(MODAL.modalActivo);
    },
    limpiar: function (modal) {
        MODAL.modalDefault();
        modal.find("#" + MODAL.idTitle).html("");
        modal.find("#" + MODAL.idBody).html("");
        modal.find("#" + MODAL.idDivButtons).html("");
    },
    disableButtons: function (btn, htmlBtn) {
        MODAL.modalActivo.find("button").each(function (i, item) {
            $(item).attr("disabled", "disabled");
        });
        MODAL.modalActivo.find("a").each(function (i, item) {
            $(item).attr("disabled", "disabled");
        });
        if (btn != null) {
            MODAL.buttonAffected = btn;
            if (htmlBtn != null) {
                MODAL.textButtonAffected = btn.html();
                btn.html(htmlBtn);
            } else {
                MODAL.textButtonAffected = btn.html();
                btn.html('<i class="fa fa-spinner fa-spin fa-lg"></i> Procesando');
            }
        }
    },
    activateButtons: function (btn, htmlBtn) {
        setTimeout(function () {
            var footer = MODAL.modalActivo.find("#" + MODAL.idFooter);
            footer.find("button").each(function (i, item) {
                $(item).removeAttr("disabled");
            });
            footer.find("a").each(function (i, item) {
                $(item).removeAttr("disabled");
            });
            if (btn != null) {
                btn.html(htmlBtn);
                return;
            }
            if (MODAL.buttonAffected != null && htmlBtn != null) {
                MODAL.buttonAffected.html(htmlBtn);
                return;
            }
            if (MODAL.buttonAffected != null) {
                MODAL.buttonAffected.html(MODAL.textButtonAffected);
            }
        }, 800);
    },
    showWait: function (msg) {
        if (msg != null) {
            MODAL.textModalWait = $("#bodyModalWait").html();
            $("#bodyModalWait").html(msg);
        }
        $("#modalWait").modal();
    },
    hideWait: function () {
        setTimeout(function () {
            $("#bodyModalWait").html(MODAL.textModalWait);
            $("#modalWait").modal('hide');
        }, 1000);
    }
};

function pause(milliseconds) {
    var dt = new Date();
    while ((new Date()) - dt <= milliseconds) {
        /* Do nothing */
    }
}

APP = {
    colorEstado: {
        CRE: "default",
        ACT: "success", MAT: "success", ABI: "success",
        ANU: "danger", BLO: "danger", INA: "danger", RHZ: "danger", RCU: "danger", RCI: "danger",
        APR: "primary", ACEP: "primary",
        OBS: "warning",
        SOL: "info",
        REE: "info"},
    getEstadoClass: function (estadoCode) {
        return "label-" + APP.colorEstado[estadoCode];
    },
    cleanForm: function (f) {
        f.find("input[type=text], textarea, #id").val("");
        f.find("input[type=checkbox]").prop("checked", false);
        f.find("input[name='*[]']").prop("checked", false);
        f.find("input[name='id*']").val("");
    },
    cleanAll: function (f) {
        f.find("input, textarea").val("");
    },
    fillFormById: function (data, f) {
        $.each(data, function (index, value) {
            f.find('#' + index).val(value);
        });
    },
    fillFormByName: function (data, f) {
        $.each(data, function (index, value) {
            f.find('[name="' + index + '"]').val(value);
        });
    },
    select2: function () {
        $(".select2single").select2({minimumResultsForSearch: -1});
        $(".select2singleclear").select2({minimumResultsForSearch: -1, allowClear: true});
        $(".select2").select2();
    },
    url: function (relative) {
        return contextPath + relative;
    },
    timePicker: {
        minuteStep: 5,
        showInputs: true,
        disableFocus: true,
        showSeconds: false,
        showMeridian: false,
    },
    disableButtonsModal: function (idFooter, btn, htmlBtn) {
        $("#" + idFooter).find("button").each(function (i, item) {
            $(item).attr("disabled", "disabled");
        });
        $("#" + idFooter).find("a").each(function (i, item) {
            $(item).attr("disabled", "disabled");
        });
        if (btn != null) {
            if (htmlBtn != null) {
                btn.html(htmlBtn);
            } else {
                btn.html('<i class="fa fa-spinner fa-spin fa-lg"></i> Procesando');
            }
        }
    },
    activatedButtonsModal: function (idFooter, btn, htmlBtn) {
        setTimeout(function () {
            $("#" + idFooter).find("button").each(function (i, item) {
                $(item).removeAttr("disabled");
            });
            $("#" + idFooter).find("a").each(function (i, item) {
                $(item).removeAttr("disabled");
            });
            if (btn != null) {
                btn.html(htmlBtn);
            }
        }, 1000);
    },
    disableButtonsModalVik: function (btn, htmlBtn) {
        $("#footerModalVik").find("button").each(function (i, item) {
            $(item).attr("disabled", "disabled");
        });
        $("#footerModalVik").find("a").each(function (i, item) {
            $(item).attr("disabled", "disabled");
        });
        if (btn != null) {
            if (htmlBtn != null) {
                btn.html(htmlBtn);
            } else {
                btn.html('<i class="fa fa-spinner fa-spin fa-lg"></i> Procesando');
            }
        }
    },
    activatedButtonsModalVik: function (btn, htmlBtn) {
        setTimeout(function () {
            $("#footerModalVik").find("button").each(function (i, item) {
                $(item).removeAttr("disabled");
            });
            $("#footerModalVik").find("a").each(function (i, item) {
                $(item).removeAttr("disabled");
            });
            if (btn != null) {
                btn.html(htmlBtn);
            }
        }, 1000);
    },
    activatedRapidoButtonsModalVik: function (btn, htmlBtn) {
        $("#footerModalVik").find("button").each(function (i, item) {
            $(item).removeAttr("disabled");
        });
        $("#footerModalVik").find("a").each(function (i, item) {
            $(item).removeAttr("disabled");
        });
        if (btn != null) {
            btn.html(htmlBtn);
        }
    },
    verModalWait: function (msg) {
        if (msg != null) {
            $("#bodyModalWait").html(msg);
        }
        $("#modalWait").modal();
    },
    cerrarModalWait: function () {
        setTimeout(function () {
            $("#modalWait").modal('hide');
        }, 1000);
    },
    cerrarRapidoModalWait: function () {
        $("#modalWait").modal('hide');
    },
    limpiarRaros: function ($this) {
        var conte = $this.val();
        conte = conte.replace(/[\n\f\b\r\t]/g, '');
        $this.val(conte);
    },
    revisarNombre: function ($this) {
        var nom = $this.val().toLowerCase().replace(/[^a-zçñáéíóúü\s'\-]/g, '');
        nom = nom.replace(/[\n\f\b\r|,\t]/g, ' ').replace(/\s\s+/g, ' ').trim();
        nom = APP.capitalize(nom, " ");
        nom = APP.capitalize(nom, "'");
        nom = APP.capitalize(nom, "-");

        $this.val(nom);
    },
    capitalize: function (string, separator) {
        var arr = string.split(separator);
        $.each(arr, function (i, value) {
            arr[i] = value.charAt(0).toUpperCase() + value.substr(1);
        });
        return arr.join(separator);
    },
    eliminarEspacios: function ($this) {
        var conte = $this.val().replace(/[\s\n\f\b\r\t]/g, '');
        $this.val(conte);
    },
    revisarDireccion: function ($this) {
        var conte = $this.val().replace(/[\n\f\b\r\t|]/g, ' ').replace(/\s\s+/g, ' ').trim();
        $this.val(conte);
    },
    revisarEmail: function ($this) {
        var conte = $this.val().toLowerCase().replace(/[\n\f\b\r\t\s|,'"!$%&/]/g, '').trim();
        conte = APP.stripAccents(conte);
        $this.val(conte);
    },
    stripAccents: function (str) {
        var from = "àáäâèéëêìíïîòóöôùúüûñç";
        var to = "aaaaeeeeiiiioooouuuunc";
        for (var i = 0, l = from.length; i < l; i++) {
            str = str.replace(new RegExp(from.charAt(i), 'g'), to.charAt(i));
        }
        return str;
    },
    template: {
        spin: "<i class='fa fa-spinner fa-spin' aria-hidden='true'></i>",
        spincenter: "<div class='text-center'><i class='fa fa-spinner fa-spin' aria-hidden='true'></i></div>",
        dynadiv: "<div class='panel-body'><div class='row' id='dynatable'></div></div>",
        inext: "<i class='fa fa-chevron-right' aria-hidden='true'></i>",
        iprev: "<i class='fa fa-chevron-left' aria-hidden='true'></i>",
        wait: '<div class="m-t m-b text-center"><i class="fa fa-spinner fa-spin fa-2x"></i>&nbsp; <span class="h3 bold">Espere un momento por favor...</span>',
        color: '<div class="colorpicker dropdown-menu"><div class="colorpicker-saturation"><i><b></b></i></div><div class="colorpicker-hue"><i></i></div><div class="colorpicker-color"><div /></div><div class="colorpicker-selectors"></div></div>'
    },
    recDynatable: function (dynatable, e) {
        var self = $(e.currentTarget);
        var tr = self.closest("tr");
        var idx = tr.attr("rel");
        var rec = dynatable.settings.dataset.records[idx];
        return rec;
    },
    goUrlReturn(url) {
        var myform = document.createElement("form");
        myform.action = APP.url(url);
        myform.method = "GET";

        var origen = location.pathname + location.search;
        product = document.createElement("input");
        product.value = Usuario.b64EncodeUnicode(origen);
        product.name = "origen";

        myform.appendChild(product);
        document.body.appendChild(myform);
        myform.submit();
    },
    validateMultiSelect: function (form) {
        form.find(".multiselect__input").each(function () {
            $(this).attr("required", true);
        });
        form.find('.multiselect__input').each(function () {
            var input = $(this);
            let element = input.closest('.multiselect').find('.multiselect__single');

            if (element.css('display') != 'none' && element.html() != "") {
                $(this).removeAttr("required");
            }
        });
    },
    wait(ms) {
        var start = new Date().getTime();
        var end = start;
        while (end < start + ms) {
            end = new Date().getTime();
        }
    },
    downloadFileHelper(response, window, document) {
        var namee = response
                .headers["content-disposition"]
                .replace("attachment; filename=", "")
                .replace(/"/g, '');
        const url = window.URL.createObjectURL(new Blob([response.data]));
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', namee);
        document.body.appendChild(link);
        link.click();
    }
};

VUE = {
    revisarEmail(string) {
        if (string == undefined) {
            return "";
        }
        var conte = string.toLowerCase().replace(/[\n\f\b\r\t\s|,'"!$%&/]/g, '').trim();
        return APP.stripAccents(conte).toLowerCase();
    },
    revisarApellido(string) {
        if (string == undefined) {
            return "";
        }
        var nom = string.toLowerCase().replace(/[^a-zçñáéíóúü\s'\-]/g, '');
        nom = nom.replace(/[\n\f\b\r|,\t]/g, ' ').replace(/\s\s+/g, ' ').trim();
        nom = APP.capitalize(nom, " ");
        nom = APP.capitalize(nom, "'");
        nom = APP.capitalize(nom, "-");

        return nom;
    },
    revisarNombreObjeto(string) {
        if (string == undefined) {
            return "";
        }
        var nom = string.replace(/[^A-Za-zÇÑÁÉÍÓÚÜçñáéíóúü,\s'\-]/g, '');
        nom = nom.replace(/[\n\f\b\r|\t]/g, ' ').replace(/\s\s+/g, ' ').trim();
        return nom.charAt(0).toUpperCase() + nom.substr(1);
    },
    revisarCodigo(string) {
        if (string == undefined) {
            return "";
        }
        var str = string.replace(/[\s\n\f\b\r\t]/g, '');
        return str.toUpperCase();
    },
    revisarAnexos(string) {
        if (string == undefined) {
            return "";
        }
        var str = string.replace(/[\-\/\,]/g, ' ');
        str = str.replace(/[^0-9\s]/g, '');
        str = str.replace(/ +(?= )/g, '').trim();
        var arr = str.split(" ");
        str = arr.join(" / ");
        return str;
    },
    revisarTelefonos(string) {
        if (string == undefined) {
            return "";
        }
        var str = string.replace(/[\/\,]/g, ' ');
        str = str.replace(/[^0-9\s\-]/g, '');
        str = str.replace(/ +(?= )/g, '').trim();
        str = str.replace(/ - /g, '-');
        var arr = str.split(" ");
        str = arr.join(" / ");
        return str;
    },
}

var htmlProcessing = '<i class="fa fa-spinner fa-pulse fa-fw"></i> Procesando...';
VUE_MODAL = {
    dataFormAjax: {
        btnclose: false,
        showaccept: true,
        dataBackdrop: "static",
        dataKeyboard: "false",
        okbtnprocessing: htmlProcessing,
        disabledok: false
    },
    dataProgress: {
        btnclose: false,
        showaccept: true,
        dataBackdrop: "static",
        dataKeyboard: "false",
        bodyBlocker: false
    },
    dataFormSimple: {
        showaccept: true,
        bodyBlocker: false
    },
    dataAlert: {
        showaccept: true,
        bodyBlocker: false,
        cancelbtn: "Aceptar",
        cancelclass: "btn-primary"
    },
    dataConfirm: {
        id: "idConfirmAction",
        btnclose: false,
        showaccept: true,
        confirm: true,
        bodyBlocker: false,
        okbtn: "Si, proceder",
        okclass: "btn-success",
        dataBackdrop: "static",
        dataKeyboard: "false",
        message: "¿Está seguro que desea ejecutar esta operación?",
        okbtnprocessing: htmlProcessing,
        okaction: function () {}
    },
    dataError: {
        showaccept: true,
        bodyBlocker: false,
        cancelbtn: "Aceptar",
        cancelclass: "btn-warning"
    },
    dataInfo: {
        footer: false
    },
    structByType(type, params) {
        var data = {};
        if (type == "FORM-AJAX") {
            data = Object.assign({}, VUE_MODAL.dataFormAjax);
        } else if (type == "PROGRESS") {
            data = Object.assign({}, VUE_MODAL.dataProgress);
        } else if (type == "FORM-SIMPLE") {
            data = Object.assign({}, VUE_MODAL.dataFormSimple);
        } else if (type == "ALERT") {
            data = Object.assign({}, VUE_MODAL.dataAlert);
        } else if (type == "CONFIRM") {
            data = Object.assign({}, VUE_MODAL.dataConfirm);
        } else if (type == "ERROR") {
            data = Object.assign({}, VUE_MODAL.dataError);
        } else if (type == "INFO") {
            data = Object.assign({}, VUE_MODAL.dataInfo);
        }
        var keys = Object.keys(params);
        for (var i = 0; i < keys.length; i++) {
            var key = keys[i];
            data[key] = params[key];
        }
        return data;
    },
    structFormAjax(params) {
        return VUE_MODAL.structByType("FORM-AJAX", params);
    },
    structProgress(params) {
        return VUE_MODAL.structByType("PROGRESS", params);
    },
    structFormSimple(params) {
        return VUE_MODAL.structByType("FORM-SIMPLE", params);
    },
    structAlert(params) {
        return VUE_MODAL.structByType("ALERT", params);
    },
    structConfirm(params) {
        console.log("3kj4n5kj3453kj4")
        return VUE_MODAL.structByType("CONFIRM", params);
    },
    structError(params) {
        return VUE_MODAL.structByType("ERROR", params);
    },
    structInfo(params) {
        return VUE_MODAL.structByType("INFO", params);
    }
}

MESSAGES = {
    errorComunicacion: 'Error de conexión con el servidor.',
    confirmDelete: '¿Seguro que desea eliminar?',
    confirmActive: '¿Seguro que desea activar?',
    confirmDesActive: '¿Seguro que desea desactivar?',
    confirmReject: '¿Seguro que desea rechazar?',
    confirmApprove: '¿Seguro que desea aprobar?',
    confirmObserve: '¿Seguro que desea observar?',
    confirmAccept: '¿Seguro que desea aceptar?',
};


APP.select2();

$.fn.disableButtonsModal = function (btn, htmlBtn)
{
    var el = this;

    $(el).find(".modal-footer>button").each(function (i, item) {
        $(item).attr("disabled", "disabled");
    });
    $(el).find(".modal-footer>a").each(function (i, item) {
        $(item).attr("disabled", "disabled");
    });
    if (btn != null) {
        if (htmlBtn != null) {
            btn.html(htmlBtn);
        } else {
            btn.html('<i class="fa fa-spinner fa-spin fa-lg"></i> Procesando');
        }
    }
    return el;
};

$.fn.enableButtonsModal = function (btn, htmlBtn)
{
    var el = this;

    setTimeout(function () {

        $(el).find(".modal-footer>button").each(function (i, item) {
            $(item).removeAttr("disabled");
        });

        $(el).find(".modal-footer>a").each(function (i, item) {
            $(item).removeAttr("disabled");
        });

        if (btn != null) {
            btn.html(htmlBtn);
        }

    }, 1000);

    return el;
};

$.fn.cleanForm = function ()
{
    $(this).find("input[type=text], textarea, #id, input[name='id'] ").val("");
    $(this).find("input[type=checkbox]").prop("checked", false);
    $(this).find("input[type=radio]").prop("checked", false);
    $(this).find("input[name='*[]']").prop("checked", false);
    $(this).find("input[name='id*']").val("");
    return this;
};

$.fn.cleanAll = function ()
{
    $(this).find("input, textarea").val("");
    return this;
};

$.fn.treeview = function () {
    var self = $(this);
    self.find('li:has(ul)').addClass('parent_li').find(' > div > span').attr('title', 'Colapsar este menú');
    self.find('li.parent_li > div > span').on('click', function (e) {
        var yourself = $(e.currentTarget);
        var children = yourself.parent("div").parent('li.parent_li').find(' > ul > li');
        if (children.is(":visible")) {
            children.hide('fast');
            yourself.attr('title', 'Expandir este menú').find(' > i').addClass('fa-folder-o').removeClass('fa-folder-open-o');
        } else {
            children.show('fast');
            yourself.attr('title', 'Colapsar este menú').find(' > i').addClass('fa-folder-open-o').removeClass('fa-folder-o');
        }
        e.stopPropagation();
    });
};

$.fn.btnEnable = function () {
    $(this).removeProp("disabled");
    $(this).html($(this).data("btn-nombre"));
};

$.fn.btnDisabled = function () {
    $(this).data("btn-nombre", $(this).prop('innerHTML'));
    $(this).prop("disabled", true);
    $(this).html('<i class="fa fa-spinner fa-spin"></i>  ' + $(this).data("btn-nombre"));
};
$.fn.upperCase = function () {
    $(this).keyup(function (e) {
        var str = $(this).val();
        $(this).val(str.toUpperCase());
    });
};
var $global = new Vue({});

const AXIOS = axios.create({});

AXIOS.interceptors.response.use(function (response) {
    if (!response.data.success) {
        notify(response.data.message, 'error');
    } else {
        if (response.data.message) {
            notify(response.data.message, 'info');
        }
    }
    return response;
}, function (error) {
    notify(MESSAGES.errorComunicacion, 'error');
    return Promise.reject(error);
});

Vue.filter('currency', function (value) {
    if (Number.isNaN(parseFloat(value))) {
        return '';
    }
    return parseFloat(value).toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ",");
});

/**
 *
 *  Base64 encode / decode
 *  http://www.webtoolkit.info/
 *
 **/
var Base64 = {

// private property
    _keyStr: "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=",

// public method for encoding
    encode: function (input) {
        var output = "";
        var chr1, chr2, chr3, enc1, enc2, enc3, enc4;
        var i = 0;

        input = Base64._utf8_encode(input);

        while (i < input.length) {

            chr1 = input.charCodeAt(i++);
            chr2 = input.charCodeAt(i++);
            chr3 = input.charCodeAt(i++);

            enc1 = chr1 >> 2;
            enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
            enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
            enc4 = chr3 & 63;

            if (isNaN(chr2)) {
                enc3 = enc4 = 64;
            } else if (isNaN(chr3)) {
                enc4 = 64;
            }

            output = output +
                    this._keyStr.charAt(enc1) + this._keyStr.charAt(enc2) +
                    this._keyStr.charAt(enc3) + this._keyStr.charAt(enc4);

        }

        return output;
    },

// public method for decoding
    decode: function (input) {
        var output = "";
        var chr1, chr2, chr3;
        var enc1, enc2, enc3, enc4;
        var i = 0;

        input = input.replace(/[^A-Za-z0-9\+\/\=]/g, "");

        while (i < input.length) {

            enc1 = this._keyStr.indexOf(input.charAt(i++));
            enc2 = this._keyStr.indexOf(input.charAt(i++));
            enc3 = this._keyStr.indexOf(input.charAt(i++));
            enc4 = this._keyStr.indexOf(input.charAt(i++));

            chr1 = (enc1 << 2) | (enc2 >> 4);
            chr2 = ((enc2 & 15) << 4) | (enc3 >> 2);
            chr3 = ((enc3 & 3) << 6) | enc4;

            output = output + String.fromCharCode(chr1);

            if (enc3 != 64) {
                output = output + String.fromCharCode(chr2);
            }
            if (enc4 != 64) {
                output = output + String.fromCharCode(chr3);
            }

        }

        output = Base64._utf8_decode(output);

        return output;

    },

// private method for UTF-8 encoding
    _utf8_encode: function (string) {
        string = string.replace(/\r\n/g, "\n");
        var utftext = "";

        for (var n = 0; n < string.length; n++) {

            var c = string.charCodeAt(n);

            if (c < 128) {
                utftext += String.fromCharCode(c);
            } else if ((c > 127) && (c < 2048)) {
                utftext += String.fromCharCode((c >> 6) | 192);
                utftext += String.fromCharCode((c & 63) | 128);
            } else {
                utftext += String.fromCharCode((c >> 12) | 224);
                utftext += String.fromCharCode(((c >> 6) & 63) | 128);
                utftext += String.fromCharCode((c & 63) | 128);
            }

        }

        return utftext;
    },

// private method for UTF-8 decoding
    _utf8_decode: function (utftext) {
        var string = "";
        var i = 0;
        var c = c1 = c2 = 0;

        while (i < utftext.length) {

            c = utftext.charCodeAt(i);

            if (c < 128) {
                string += String.fromCharCode(c);
                i++;
            } else if ((c > 191) && (c < 224)) {
                c2 = utftext.charCodeAt(i + 1);
                string += String.fromCharCode(((c & 31) << 6) | (c2 & 63));
                i += 2;
            } else {
                c2 = utftext.charCodeAt(i + 1);
                c3 = utftext.charCodeAt(i + 2);
                string += String.fromCharCode(((c & 15) << 12) | ((c2 & 63) << 6) | (c3 & 63));
                i += 3;
            }

        }

        return string;
    }

}
