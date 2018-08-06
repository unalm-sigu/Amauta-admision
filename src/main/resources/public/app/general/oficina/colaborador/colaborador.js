Vue.component("multiselect", window.VueMultiselect.default)
let $dynatable = null;
$('#dynaTable').dynatable({});


Vue.component("dynatable", {
    template: "#dynatableTemplate",
    props: ["project", "dynatable"],
    mounted: function () {
        let $vue = this;
        $vue.oficina = JSON.parse(oficinaId);
        $vue.createDynatable();
        $global.$on("reloadDyntable", function () {
            $dynatable.process();
        });
        $global.$on("oficinaId", function (valor) {
            $vue.oficina = valor.id;
            $dynatable.process();
        });
        $('.dynatable-search').addClass('col-md-2 pull-right');
        $('.dynatable-search').find('input')
                .addClass('form-control input-sm')
                .attr('placeholder', 'Buscar');

        $('multiselect').select2({
            placeholder: {
                id: $vue.oficina, // the value of the option
            }
        });
    },
    methods: {
        createDynatable: function () {
            let $vue = this;

            $dynatable = $('#dynaTable').dynatable({
                dataset: {
                    ajaxUrl: APP.url("general/oficina/" + $vue.oficina + '/listColaboradores'),
                    perPageDefault: 10
                },
                writers: {_rowWriter: $vue.writter},
                table: {bodyRowSelector: "tbody tr"}
            }).data('dynatable');

            $("body").delegate(".updateEstado", "click", function () {
                $global.$emit("updateEstado", $(this).attr("rel"), $(this).text(), $(this).attr("value"));
            });
            $("body").delegate(".updateColaborador", "click", function () {
                $global.$emit("updateColaborador", $(this).attr("rel"));
            });
            $("body").delegate(".updatePersona", "click", function () {
                $global.$emit("updatePersona", $(this).attr("rel"));
            });


            var divElegido = null;
            $("body").delegate(".verModalidades", "click", function (e) {
                let $this = $(this);
                var div = $this.closest("div");
                var classColor = 'bg-light';
                var tieneBgColor = div.hasClass(classColor);
                $dynatable.queries.remove("search");
                if (divElegido !== null) {
                    divElegido.removeClass(classColor);
                    divElegido = null;
                }

                if (!tieneBgColor) {
                    div.addClass(classColor);
                    var estado = $this.attr("rel");
                    $dynatable.queries.add("search", estado);
                    divElegido = div;
                }
                $dynatable.process();
            });
        },
        writter: function (rowIndex, record, columns, cellWriter) {
            record.index = rowIndex;
            var html = $.templates("#dynatableRowTemplate").render(record);
            return $(html).prop('outerHTML');
        }
    }
});
new Vue({
    el: '#colaboradorVue',
    data: {
        oficinas: JSON.parse(oficinasJson),
        oficina: {id: JSON.parse(oficinaId)},
        listaCargos: JSON.parse(cargosJson),
        persona: {},
        colaborador: {},
        perfilCompania: {}
    },
    computed: {

    },
    created() {
        let $vue = this;
        console.log($vue.listaCargos);
        $vue.oficinas.forEach(function (elem) {
            if ($vue.oficina.id == elem.id) {
                $vue.oficina = elem;
            }
        })
    },
    mounted: function () {
        let $vue = this;
        $global.$on("updateEstado", function (id, value, estado) {
            $vue.updateEstado(id, value, estado);
        });
        $global.$on("updateColaborador", function (id) {
            $vue.updateColaborador(id);
        });
        $global.$on("updatePersona", function (id) {
            $vue.updatePersona(id);
        });

    },
    methods: {
        updatePersona(id){
              window.location = APP.url('general/persona/'+id+'/edicion?origen=' + window.location.pathname);
        },
        addCargo: function () {
            let $vue = this;
            var flag = false;
            $vue.listaCargos.forEach(function (elem) {
                if (elem.nombre == $vue.perfilCompania.nombre) {
                    notify('El cargo ingresado ya existe', "error");
                    flag = true;
                }
            })
            if (flag) {
                return;
            }
            $vue.perfilCompania.oficinaContiene = $vue.oficina;
            $.ajax({
                url: APP.url('general/oficina/cargo'),
                type: 'POST',
                contentType: "application/json",
                data: JSON.stringify($vue.perfilCompania),
                success: function (response) {
                    notify(response.message, "info");
                    $global.$emit("reloadDyntable");
                    $("#myModal").modal('hide');
                },
                error: function (error) {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        nuevoCargos: function () {
            $("#myModal").modal('show');
        },
        regresar: function () {
            location.href = APP.url("general/oficina");
        },
        updateColaborador: function (id) {
            let $vue = this;
            $vue.oficina
            location.href = APP.url("general/oficina/" + id + "/updateColaborador/" + $vue.oficina.id)
        },
        nuevoColaborador: function () {
            let $vue = this;
            $vue.oficina
            location.href = APP.url("general/oficina/" + $vue.oficina.id + "/nuevoColaborador")

        },
        oficinaSeleccionada: function () {
            let $vue = this;
            console.log($vue.oficina);
            $global.$emit("oficinaId", $vue.oficina);
        },
        updateEstado: function (id, value, estado) {
            let $vue = this;
            console.log(estado);
            if (estado == "DESP") {
                location.href = APP.url("general/oficina/" + id + "/updateColaborador/" + $vue.oficina.id);
                return;
            }
            $vue.colaborador = {id: id, estado: value, oficina: $vue.oficina};
            bootbox.confirm({
                message: "¿Seguro desea cambiar de estado al colaborador?",
                buttons: {
                    confirm: {label: "Si, seguro", className: "btn-info"},
                    cancel: {label: "No", className: "btn-link"}
                },
                callback: function (result) {
                    if (!result) {
                        return;
                    }
                    $.ajax({
                        url: APP.url('general/oficina/updateEstado'),
                        type: 'POST',
                        contentType: "application/json",
                        data: JSON.stringify($vue.colaborador),
                        success: function (response) {
                            notify(response.message, "info");
                            $global.$emit("reloadDyntable");
                        },
                        error: function (error) {
                            notify(MESSAGES.errorComunicacion, "error");
                        }
                    });
                }
            });
        }
    }
});
