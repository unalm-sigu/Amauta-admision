$(function () {
    Contenido = {
        Init: function () {
            if ($("#tipo").val() == 'CONT') {
                CKEDITOR.replace('contenido', {height: 380});
            }
            $('#fileupload').fileupload({
                url: APP.url('archivo/upload'),
                maxNumberOfFiles: 1,
                dataType: 'json',
                add: function (e, data) {
                    if (data.files[0].type.search(/(\.|\/)(jpe?g|png)$/i) == -1) {
                        notify("Formato de archivo no soportado.", "error");
                        return;
                    }
                    data.submit();
                },
                progress: function (e, data) {
                    var progress = parseInt(data.loaded / data.total * 100, 10);
                    if (progress === 100) {
                    }
                },
                done: function (e, data) {
                    $('input:submit').removeAttr("disabled");
                    if (data.result.success) {

                        var fileName = data.result.data;
                        $('#imagen').attr("src", APP.url("archivo/downloadfn/" + fileName));
                        $('#imgUrl').val(fileName);
                        console.log(fileName);
                        notify(data.result.message, "info");

                    } else {
                        notify(data.result.message, "error");
                    }

                },
                fail: function (e, data) {
                    $('input:submit').removeAttr("disabled");
                    notify(data.result.message, "error");
                }
            });
        },
        updateImg() {
            var img = $("#imgUrl").val();
            var idCont = $("#idCont").val();
            if (img == '' || idCont == '') {
                notify("No ha cargado ninguna imagen", 'warning');
                return;
            }
            $.ajax({
                method: 'POST',
                url: APP.url('configuracion/editorcontenido/updateImg'),
                data: {idContenido: idCont, fileName: img},
                success: function (response) {
                    if (response.success) {
                        notify(response.message, 'info');
                    } else {
                        notify(response.message, 'error');
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        previsualizacion() {
            var idCont = $("#idCont").val();
            var url = location.protocol + '//' + location.host + APP.url('configuracion/editorcontenido/' + idCont + '/ver');
            MODAL.init("lg");
            MODAL.title("");
            MODAL.body('<iframe src="' + url + '" width="100%" frameborder="0" style="border:1px solid #AEBDCD;"></iframe>');
            MODAL.show();
        }

    };
    Contenido.Init();
    $("body").delegate("#updateContenido", "click", function (e) {
        Contenido.updateContenido();
    });
    $("body").delegate("#preview", "click", function (e) {
        Contenido.previsualizacion();
    });
    $("body").delegate("#updateImg", "click", function (e) {
        Contenido.updateImg();
    });
});

Vue.component("multiselect", window.VueMultiselect.default);
new Vue({
    el: '#contenidoVUE',
    data: {
        contenido: JSON.parse(contenidoJson),
        variablesCarta: JSON.parse(variablesCartaJson),
        variables: JSON.parse(variablesJson),
        sistemas: JSON.parse(sistemasJson),
        contVariable: {},
        variable: {},
        sistema: {}
    },
    mounted: function () {
        let $vue = this;
        $vue.sistema = $vue.contenido.sistema;
    },
    methods: {
        updateContenido() {
            let $vue = this;
            for (instance in CKEDITOR.instances) {
                CKEDITOR.instances[instance].updateElement();
            }
            let contenido = $("#contenido").val();
            $vue.contenido.contenido = contenido;
            axios.post(APP.url('configuracion/editorcontenido/updateContenido'), $vue.contenido)
                    .then(response => {
                        if (response.data.success) {
                            notify(response.data.message, 'info');
                        } else {
                            notify(response.data.message, 'error');
                        }
                    }).catch(e => {
                notify(MESSAGES.errorComunicacion, "error");
            })
        },
        reloadVariables() {
            let $vue = this;
            $.ajax({
                method: 'POST',
                url: APP.url('configuracion/editorcontenido/' + $vue.contenido.id + '/allVariables'),
                success: function (response) {
                    if (response.success) {
                        $vue.contVariable = {};
                        $vue.variablesCarta = response.data;
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        addVariable() {
            let $vue = this;
            $.ajax({
                method: 'POST',
                url: APP.url('configuracion/editorcontenido/' + $vue.contenido.id + '/addVariable'),
                data: JSON.stringify($vue.contVariable),
                dataType: "json",
                contentType: "application/json",
                success: function (response) {
                    if (response.success) {
                        $vue.reloadVariables();
                        notify(response.message, "info");
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });

        },
        deleteVariable(item) {
            let $vue = this;
            console.log(item);
            $.ajax({
                method: 'POST',
                url: APP.url('configuracion/editorcontenido/' + item.id + '/deleteVariable'),
                success: function (response) {
                    if (response.success) {
                        $vue.reloadVariables();
                        notify(response.message, "info");
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        updateVariable(item) {
            let $vue = this;
            console.log(item);
            $.ajax({
                method: 'POST',
                url: APP.url('configuracion/editorcontenido/updateVariable'),
                data: JSON.stringify(item),
                dataType: "json",
                contentType: "application/json",
                success: function (response) {
                    if (response.success) {
                        $vue.reloadVariables();
                        notify(response.message, "info");
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        }
    }
});