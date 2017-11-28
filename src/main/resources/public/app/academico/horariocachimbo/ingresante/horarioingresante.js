$(function () {
    var $global = new Vue({});
    let  dynatableRow = null;
    
    var DynatableRowTemplate = Vue.component("dynatableRow", {
        template: "#dynatableRowTemplate",
        data: function () {
            return {
                horario: [{
                        id: 0,
                        estudiante: "",
                        carrera: "",
                        facultatd: "",
                        horario: "",
                        estado: ""
                    }]
            }
        },
    });
    
    let  dynatable = null;
    Vue.component("dynatable", {
        template: "#dynatableTemplate",
        props: ["horario"],
        mounted: function () {
            var $vue = this;
            $vue.createDynatable();
        },
        methods: {
            createDynatable: function () {
                var $vue = this;
                dynatable = $('#dynaTable').dynatable({
                    dataset: {
                        ajaxUrl: APP.url('academico/horariocachimbo/ingresante/list'),
                        perPageDefault: 4,
                        ajaxData: {id: $vue.horario},
                    },
                    writers: {_rowWriter: $vue.writter},
                    table: {bodyRowSelector: "tbody tr"}
                }).data('dynatable');
                
                
                dynatable.bind("dynatable:afterUpdate",function(){

                    $("body").delegate(".buscarHorario", "click", function (e) {
                        console.log('hola ssssssssssssssssss');
                        $global.$emit("buscarHorario", e);
                    });
                    
                });

            },
            writter: function (rowIndex, record, columns, cellWriter) {
                var dynatableRowTemplate = new DynatableRowTemplate();
                dynatableRowTemplate.horario = record;
                var component = dynatableRowTemplate.$mount();
                var el = component.$el;
                return el.innerHTML;
            }
        }
    });
    new Vue({
        el: '#main',
        data: {
            horario: {}
        },
        created() {
            let $vue = this;
        },
        methods: {
            labeled(estado) {
                return estado == 'ACTIVO' ? 'label-success' : 'label-danger';
            },
            nuevo() {
                this.curso = {id: null, nombre: '', codigo: '', estado: 'INACTIVO'};
                var mimodal = bootbox.confirm({
                    title: "Nuevo Alumno",
                    message: APP.template.spincenter,
                    buttons: {
                        confirm: {label: "Guardar", className: "btn-info"},
                        cancel: {label: "Cancelar", className: "btn-link"}
                    },
                    callback: function (result) {
                        if (result) {
                            if (mimodal.find('form').parsley().validate() == true) {
                            }
                        } else {
                            mimodal.modal('hide');
                        }
                        return false;
                    }
                });
                $.ajax({
                    url: APP.url('academico/horario/nuevo'),
                    type: 'POST',
                    async: true,
                    success: function (response) {
                        if (response.success) {
                            mimodal.find('.bootbox-body').html(response.data);
                            mimodal.find('[name="tipoCiclo"]').select2({minimumResultsForSearch: -1});
                        } else {
                            notify(response.message, "error");
                            mimodal.modal('hide');
                        }
                    },
                    error: function () {
                        mimodal.modal('hide');
                        notify(MESSAGES.errorComunicacion, "error");
                    }
                });
            },
            update(item) {
                this.curso = item;
            },
            save() {
                let $vue = this;
                $vue.cursos.push($vue.curso);
            },
            remove(item) {
                let $vue = this;
                $vue.cursos.pop(item);
            },
            buscarHorario(id) {
                console.log('buscarrrrrrrrrrrr');
                console.log(id);
            }
        }
    });
});
