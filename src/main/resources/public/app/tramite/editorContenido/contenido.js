Vue.component("multiselect", window.VueMultiselect.default);
new Vue({
    el: '#main',
    data: {
        alumno: {id: null},
        contenidoPreview: null,
        variablePlantilla: JSON.parse(variablePlantillaJson),
        variables: JSON.parse(variablesJson),
        id: JSON.parse(id),
        dataModalPreview: {
            id: 'modalPreview',
            header: true,
            title: 'Vista previa',
            okbtn: 'Vista previa',
            modalsize: 'modal-lg',
            modalscroll: 'modal-scroll-600',
            showaccept: false
        },
        contVariable: {}
    },
    mounted() {
        let vue = this;
        CKEDITOR.replace('contenido', {height: 380});
    },
    methods: {
        updateContenido: function () {
            let vue = this;
            for (instance in CKEDITOR.instances) {
                CKEDITOR.instances[instance].updateElement();
            }
            $.ajax({
                method: 'POST',
                url: APP.url('tramite/plantillaconstancia/updateContenido'),
                data: $('form').serialize(),
                success: function (response) {
                    if (response.success) {
                        notify(response.message, 'info');
                        vue.variables = response.data.variablePlantilla;
                    } else {
                        notify(response.message, 'error');
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        previewHtml: function () {

            let vue = this;
            $global.$emit('MODAL-WAIT-OPEN');

            $.ajax({
                method: 'POST',
                url: APP.url('tramite/plantillaconstancia/preview'),
                data: $('form').serialize(),
                success: function (response) {
                    if (response.success) {
                        $global.$emit('MODAL-WAIT-CLOSE');
                        vue.$refs.modalPreview.open();
                        vue.contenidoPreview = response.data;
                        var myFrame = $("#myframe").contents().find('body');
                        myFrame.html(response.data);
                    } else {
                        $global.$emit('MODAL-WAIT-CLOSE');
                        notify(response.message, 'error');
                    }
                },
                error: function () {
                    $global.$emit('MODAL-WAIT-CLOSE');
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });

        },
        reloadVariables() {
            let $vue = this;
            $.ajax({
                method: 'POST',
                url: APP.url('tramite/plantillaconstancia/' + $vue.id + '/allVariable'),
                success: function (response) {
                    if (response.success) {
                        $vue.contVariable = {};
                        $vue.variablePlantilla = response.data;
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
            $vue.contVariable.plantillaDocumentoAcademico = {};
            $vue.contVariable.plantillaDocumentoAcademico.id = $vue.id;
            $.ajax({
                method: 'POST',
                url: APP.url('tramite/plantillaconstancia/saveVariable'),
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
                url: APP.url('tramite/plantillaconstancia/' + item.id + '/deleteVariable'),
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
            item.plantillaDocumentoAcademico = {};
            item.plantillaDocumentoAcademico.id = $vue.id;
            $.ajax({
                method: 'POST',
                url: APP.url('tramite/plantillaconstancia/updateVariable'),
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
        },
        saveModalPreview: function () {

        }
    }
});
