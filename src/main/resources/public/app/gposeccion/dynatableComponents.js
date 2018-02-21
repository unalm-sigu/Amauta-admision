Vue.component("dynatable", {
    template: "#dynatableTemplate",
    props: {
        project: {required: false},
        dynatable: {required: false},
        onclick: {type: Function, default: () => {
            }},
        gruposeleccionado: {required: false, default: null}
    },
    mounted: function () {
        let $vue = this;
        $global.$on("reloadDynaZeta", function () {
            if ($vue.dynatable == null) {
                $vue.createDynatable();
            }

            if ($vue.dynatable != null && $vue.dynatable.queries != null) {
                $vue.dynatable.queries.remove("search");
                $vue.dynatable.process();
            }
        });

        $global.$on("seleccionarGrupoZeta", function (grupoSel) {
            if ($vue.dynatable != null && $vue.dynatable.queries != null) {
                $vue.dynatable.queries.add("search", grupoSel.codigo);
                $vue.dynatable.process();
            }
            $vue.clearAndSelect(grupoSel);
        });

        $global.$on("clearAndSelectZeta", function () {
            $vue.clearAndSelect();
        });

    },
    methods: {
        createDynatable: function () {
            let $vue = this;

            $vue.dynatable = $('#dynaTable').dynatable({
                dataset: {
                    ajaxUrl: APP.url('academico/gposeccion/listGrupoHorariosZetas'),
                    perPageDefault: 6,
                    ajaxData: {tipoGrupoHora: "ZETA"}

                },
                writers: {_rowWriter: $vue.writter},
                table: {bodyRowSelector: 'div'},
                features: {
                    pushState: false,
                    recordCount: false
                },
                inputs: {
                    processingText: '<i class="fa fa-spinner fa-spin"></i> Cargando información...'
                }
            }).data('dynatable');

            $('#dynaTable').bind('dynatable:afterUpdate', function (e, dynatable) {
                $("[name='dvGruposZeta']").each(function () {
                    let grupo = $(this).attr("rel");
                    $(this).removeClass("active");
                    if ($vue.gruposeleccionado != null && $vue.gruposeleccionado.id == grupo) {
                        $(this).addClass("active");
                    }
                });
            });

            $("body").delegate(".cls-grupos-sel", "click", function (e) {
                e.preventDefault();
                let grupoSeleccionado = $(this).attr("rel");
                $vue.onclick(grupoSeleccionado);
            });



        },
        writter: function (rowIndex, record, columns, cellWriter) {
            var labelColor = {ACT: 'success', INA: 'danger'};
            var labelName = {ACT: 'Activo', INA: 'Inactivo'};
            record.colorEstado = labelColor[record.estado];
            record.nameEstado = labelName[record.estado];

            var html = $.templates("#dynatableRowTemplate").render(record);
            var outerHTML = $(html).prop('outerHTML');

            return outerHTML;
        },
        clearAndSelect(grupoSel) {
            $("[name='dvGruposZeta']").each(function () {
                let grupo = $(this).attr("rel");
                $(this).removeClass("active");
                if (grupoSel != null && (grupoSel.id == grupo)) {
                    $(this).addClass("active");
                }
            });
        }, clickGrupo() {

        }
    },
    watch: function () {
    }
});


Vue.component("dynatable-especial", {
    template: "#dynatableTemplateEspecial",
    // props: ["project", "dynatable"],
    props: {
        project: {required: false},
        dynatable: {required: false},
        onclick: {type: Function, default: () => {
            }},
        gruposeleccionado: {required: false, default: null}
    },
    mounted: function () {
        let $vue = this;
        $global.$on("reloadDynaEspecial", function (seccion) {
            if ($vue.dynatable == null) {
                $vue.createDynatable();
                $vue.dynatable.queries.add("seccion", seccion);
                $vue.dynatable.process();
            }
            if ($vue.dynatable != null && $vue.dynatable.queries != null) {
                $vue.dynatable.queries.remove("search");
                $vue.dynatable.process();
            }
        });

        $global.$on("seleccionarGrupoEspecial", function (grupoSel) {

            if ($vue.dynatable != null && $vue.dynatable.queries != null) {
                $vue.dynatable.queries.add("search", grupoSel.codigo);
                $vue.dynatable.process();
            }
            $vue.clearAndSelect(grupoSel);
        });

        $global.$on("clearAndSelectEsp", function () {
            $vue.clearAndSelect();
        });

    },
    methods: {
        createDynatable: function () {
            let $vue = this;

            $vue.dynatable = $('#dynaTableEspecial').dynatable({
                dataset: {
                    ajaxUrl: APP.url('academico/gposeccion/listGrupoHorariosByTipoEspecial'),
                    perPageDefault: 6,
                    ajaxData: {
                        tipoGrupoHora: "ESPECIAL"
                    }
                },
                writers: {_rowWriter: $vue.writter},
                table: {bodyRowSelector: 'div'},
                features: {
                    pushState: false,
                    //   search: false,
                    recordCount: false
                },
                inputs: {
                    processingText: '<i class="fa fa-spinner fa-spin"></i> Cargando información...'
                }
            }).data('dynatable');

            $('#dynaTableEspecial').bind('dynatable:afterUpdate', function (e, dynatable) {
                $("[name='dvGruposEsp']").each(function () {
                    let grupo = $(this).attr("rel");

                    $(this).removeClass("active");

                    if ($vue.gruposeleccionado != null && $vue.gruposeleccionado.id == grupo) {
                        $(this).addClass("active");
                    }
                });
            });

            $("body").delegate(".cls-grupos-sel-esp", "click", function (e) {
                e.preventDefault();
                $vue.onclick($(this).attr("rel"));
            });



        },
        writter: function (rowIndex, record, columns, cellWriter) {
            var labelColor = {ACT: 'success', INA: 'danger'};
            var labelName = {ACT: 'Activo', INA: 'Inactivo'};
            record.colorEstado = labelColor[record.estado];
            record.nameEstado = labelName[record.estado];
            var html = $.templates("#dynatableRowTemplateEsp").render(record);
            var outerHTML = $(html).prop('outerHTML');

            return outerHTML;
        },
        clearAndSelect(grupoSel) {
            $("[name='dvGruposEsp']").each(function () {
                let grupoEach = $(this).attr("rel");
                $(this).removeClass("active");
                if (grupoSel != null && (grupoSel.id == grupoEach)) {
                    $(this).addClass("active");
                }
            });
        }, clickGrupo() {

        }
    },
    created: function () {
    }
});