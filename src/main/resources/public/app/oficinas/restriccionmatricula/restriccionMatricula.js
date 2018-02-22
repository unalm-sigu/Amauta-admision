Vue.component("dynatable", {
    template: "#dynatableTemplate",
    props: ["project", "dynatable"],
    mounted: function () {
        let $vue = this;
        $vue.createDynatable();

        $global.$on("reloadDyntable", function () {
            Logger.debug("ok, relad");
            $dynatable.process();
        });
    },
    methods: {
        createDynatable: function () {
            let $vue = this;

            $dynatable = $('#dynaTable').dynatable({
                dataset: {
                    ajaxUrl: APP.url('aporte/aportealumno/list'),
                    perPageDefault: 10
                },
                writers: {_rowWriter: $vue.writter},
                table: {bodyRowSelector: "tbody tr"}
            }).data('dynatable');

            $("body").delegate(".anular", "click", function () {
                $global.$emit("anular", $(this).attr("rel"));
            });
            $("body").delegate(".levantar", "click", function () {
                $global.$emit("levantar", $(this).attr("rel"));
            });
        },
        writter: function (rowIndex, record, columns, cellWriter) {
            record.index = rowIndex;
            record.mostrarModalidad = record.modalidadEstudio !== "Visitante" && record.modalidadEstudio !== "Especial";
            var html = $.templates("#dynatableRowTemplate").render(record);
            return $(html).prop('outerHTML');
        }
    }
});

new Vue({
    el: '#restriccionMatriculaVUE',
    data: {
    },
    computed: {
    },
    mounted: function () {
        let $vue = this;
        $global.$on("levantar", function (id) {
            $vue.levantar(id);
        });
        $global.$on("anular", function (id) {
            $vue.anular(id);
        });
    },
    methods: {
        levantar() {
            let $vue = this;
            bootbox.confirm({
                message: '¿Seguro que desea levantar la restricción de matrícula?',
                buttons: {
                    confirm: {label: 'Si, eliminar', className: "btn-danger"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        $.ajax({
                            method: 'POST',
                            async: false,
                            url: APP.url('oficinas/restriccionmatricula/levantar'),
                            data: {id: id},
                            contentType: "application/json",
                            success: function (response) {
                                if (response.success) {
                                    notify(response.message, "info");
                                    $global.$emit("reloadDyntable", response.data);
                                } else {
                                    notify(response.message, "error");
                                }
                                $vue.$refs.modalAporteAlumno.open();
                            },
                            error: function () {
                                notify(MESSAGES.errorComunicacion, "error");
                            }
                        });
                    }
                }
            })
        },
        anular(id) {
            let $vue = this;
            bootbox.confirm({
                message: '¿Seguro que desea anular la restricción de matrícula?',
                buttons: {
                    confirm: {label: 'Si, eliminar', className: "btn-danger"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        $.ajax({
                            method: 'POST',
                            async: false,
                            url: APP.url('oficinas/restriccionmatricula/anular'),
                            data: {id: id},
                            contentType: "application/json",
                            success: function (response) {
                                if (response.success) {
                                    notify(response.message, "info");
                                    $global.$emit("reloadDyntable", response.data);
                                } else {
                                    notify(response.message, "error");
                                }
                                $vue.$refs.modalAporteAlumno.open();
                            },
                            error: function () {
                                notify(MESSAGES.errorComunicacion, "error");
                            }

                        });
                    }
                }
            })
        }
    }
});







