$(function () {
    /* 
     var dynatable = $('#dynaTable').dynatable({
     dataset: {
     ajaxUrl: APP.url('academico/horario/grupo/list'),
     perPageDefault: 12,
     ajaxData: {idTipoGrupo: $('[name="idTipoGrupo"]').val()},
     },
     writers: {
     _rowWriter: ulWriter
     },
     table: {
     bodyRowSelector: 'div'
     },
     features: {pushState: false, search: true},
     inputs: {
     processingText: '<i class="fa fa-spinner fa-spin"></i> Cargando información...'
     }
     }).data('dynatable');
     
     $('#dynaTable').bind('dynatable:afterUpdate', function (e, dynatable) {
     Grupo.foccusActivo();
     });
     
     
     function ulWriter(rowIndex, record, columns, cellWriter) {
     var labelColor = {ACT: 'success', INA: 'danger'};
     var labelName = {ACT: 'Activo', INA: 'Inactivo'};
     record.colorEstado = labelColor[record.estado];
     record.nameEstado = labelName[record.estado];
     var html = $.templates("#grupoTemplate").render(record);
     var outerHTML = $(html).prop('outerHTML');
     return outerHTML;
     }
     
     
     var Grupo = {
     form: null,
     body: $('body'),
     init: function () {
     },
     grupoActivo: null,
     update: function (e) {
     
     e.preventDefault();
     var self = $(e.currentTarget);
     var id = self.attr('rel');
     
     var mimodal = bootbox.confirm({
     title: "Editar Grupo Horas",
     message: APP.template.spincenter,
     buttons: {
     confirm: {label: "Guardar", className: "btn-info"},
     cancel: {label: "Cancelar", className: "btn-link"}
     },
     callback: function (result) {
     if (result) {
     if (mimodal.find('form').parsley().validate() == true) {
     Grupo.saveGrupo(mimodal);
     }
     } else {
     mimodal.modal('hide');
     }
     return false;
     }
     });
     $.ajax({
     url: APP.url('academico/horario/grupo/update'),
     type: 'POST',
     async: true,
     data: {id: id},
     success: function (response) {
     if (response.success) {
     mimodal.find('.bootbox-body').html(response.data);
     mimodal.find('[name="tipoCiclo"]').select2({minimumResultsForSearch: -1});
     mimodal.find('[name="tipoSeccion"]').select2({minimumResultsForSearch: -1});
     mimodal.find('[name="letra"]').select2({minimumResultsForSearch: -1});
     mimodal.find('[name="conHorario"]').select2({minimumResultsForSearch: -1});
     mimodal.find('.cp').colorpicker();
     mimodal.find('[name="tipoGrupoHoras.id"]').val($("[name=idTipoGrupo]").val());
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
     nuevo: function (e) {
     e.preventDefault();
     
     var mimodal = bootbox.confirm({
     title: "Nuevo Tipo Grupo Horas",
     message: APP.template.spincenter,
     buttons: {
     confirm: {label: "Guardar", className: "btn-info"},
     cancel: {label: "Cancelar", className: "btn-link"}
     },
     callback: function (result) {
     if (result) {
     if (mimodal.find('form').parsley().validate() == true) {
     Grupo.saveGrupo(mimodal);
     }
     } else {
     mimodal.modal('hide');
     }
     return false;
     }
     });
     $.ajax({
     url: APP.url('academico/horario/grupo/nuevo'),
     type: 'POST',
     async: true,
     success: function (response) {
     if (response.success) {
     mimodal.find('.bootbox-body').html(response.data);
     mimodal.find('[name="tipoCiclo"]').select2({minimumResultsForSearch: -1});
     mimodal.find('[name="tipoSeccion"]').select2({minimumResultsForSearch: -1});
     mimodal.find('[name="letra"]').select2({minimumResultsForSearch: -1});
     mimodal.find('[name="conHorario"]').select2({minimumResultsForSearch: -1});
     mimodal.find('.cp').colorpicker({color: '#000'});
     mimodal.find('[name="tipoGrupoHoras.id"]').val($("[name=idTipoGrupo]").val());
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
     saveGrupo: function (mimodal) {
     $.ajax({
     url: APP.url('academico/horario/grupo/save'),
     type: 'POST',
     async: true,
     data: mimodal.find('form').serialize(),
     success: function (response) {
     if (response.success) {
     if (response.data.existecodigo) {
     var inputCodigo = mimodal.find('[name="codigo"]').parsley();
     window.ParsleyUI.removeError(inputCodigo, "errorValidacionCodigo");
     window.ParsleyUI.addError(inputCodigo, "errorValidacionCodigo", "Código ya registrado");
     } else {
     dynatable.process();
     Grupo.grupoActivo = null;
     $("#tablaHorario").html('');
     mimodal.modal('hide');
     }
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
     eliminar: function (e) {
     e.preventDefault();
     var self = $(e.currentTarget);
     var id = self.attr("rel");
     bootbox.confirm({
     message: "¿Está seguro que desea eliminar el grupo horas?",
     size: 'small',
     buttons: {
     confirm: {label: 'Sí, Eliminar', className: "btn-danger"},
     cancel: {label: 'Cancelar', className: "btn-link"}
     },
     callback: function (result) {
     if (result) {
     $.ajax({
     url: APP.url('academico/horario/grupo/delete'),
     type: 'POST',
     async: false,
     data: {id: id},
     success: function (response) {
     if (response.success) {
     notify(response.message, "info");
     dynatable.process();
     if (Grupo.grupoActivo == id) {
     Grupo.grupoActivo = null;
     }
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
     },
     verhorario: function (e) {
     var mibox = bootbox.dialog({message: APP.template.wait, closeButton: false});
     e.preventDefault();
     var self = $(e.currentTarget);
     var id = self.attr("rel");
     this.toggleActivo(self);
     Grupo.grupoActivo = id;
     
     this.getHorario(id);
     mibox.modal('hide');
     },
     toggleActivo: function (self) {
     var activo = $(".list-group-item.grupoactivo");
     if (activo.length > 0) {
     activo.removeClass("grupoactivo");
     }
     var patter = self.parents(".list-group-item:first");
     patter.addClass("grupoactivo");
     },
     foccusActivo: function () {
     var activo = $(".list-group-item.grupoactivo");
     if (activo.length < 1) {
     if (Grupo.grupoActivo != null) {
     var patter = $(".verhorario[rel=" + Grupo.grupoActivo + "]").parents(".list-group-item:first");
     patter.addClass("grupoactivo");
     }
     }
     },
     getHorario: function () {
     $.ajax({
     url: APP.url('academico/horario/grupo/horario'),
     type: 'POST',
     async: false,
     data: {id: Grupo.grupoActivo},
     success: function (response) {
     if (response.success) {
     $("#tablaHorario").html(response.data);
     } else {
     notify(response.message, "error");
     $("#tablaHorario").html('');
     }
     },
     error: function () {
     notify(MESSAGES.errorComunicacion, "error");
     $("#tablaHorario").html('');
     }
     });
     },
     asignarHora: function (e) {
     e.preventDefault();
     if (Grupo.grupoActivo == null) {
     return;
     }
     var mibox = bootbox.dialog({message: APP.template.wait, closeButton: false});
     var self = $(e.currentTarget);
     var hora = self.attr("rel");
     var dia = self.attr("rev");
     $.ajax({
     url: APP.url('academico/horario/grupo/asignarHora'),
     type: 'POST',
     async: false,
     data: {
     'hora.id': hora,
     'dia.id': dia,
     'grupoHorario.id': Grupo.grupoActivo
     },
     success: function (response) {
     if (response.success) {
     Grupo.getHorario();
     //                        dynatable.process();
     } else {
     notify(response.message, "error");
     }
     mibox.modal('hide');
     },
     error: function () {
     mibox.modal('hide');
     notify(MESSAGES.errorComunicacion, "error");
     $("#tablaHorario").html('');
     }
     });
     },
     desasignarHora: function ($this, e) {
     e.preventDefault();
     var letra = $this.find(".text-item-horario").text();
     var color = $this.css('border-color');
     
     if (letra != '' && color == 'rgb(203, 213, 221)') {
     notify("Esta hora - día se encuentra ocupada.", "error")
     return;
     }
     
     if (Grupo.grupoActivo == null) {
     return;
     }
     
     var mibox = bootbox.dialog({message: APP.template.wait, closeButton: false});
     var self = $(e.currentTarget);
     var id = self.data("id");
     var grupo = self.data("idgrupo");
     if (Grupo.grupoActivo != grupo) {
     return;
     }
     $.ajax({
     url: APP.url('academico/horario/grupo/desasignarHora'),
     type: 'POST',
     async: false,
     data: {
     'id': id
     },
     success: function (response) {
     if (response.success) {
     dynatable.process();
     Grupo.getHorario();
     } else {
     notify(response.message, "error");
     }
     mibox.modal('hide');
     },
     error: function () {
     mibox.modal('hide');
     notify(MESSAGES.errorComunicacion, "error");
     $("#tablaHorario").html('');
     }
     });
     }
     };
     
     Grupo.body.delegate('#nuevo', 'click', function (e) {
     Grupo.nuevo(e);
     });
     
     Grupo.body.delegate('.eliminar', 'click', function (e) {
     Grupo.eliminar(e);
     });
     
     Grupo.body.delegate('.editar', 'click', function (e) {
     Grupo.update(e);
     });
     
     Grupo.body.delegate('.verhorario', 'click', function (e) {
     Grupo.verhorario(e);
     });
     
     Grupo.body.delegate('.asignar-hora', 'dblclick', function (e) {
     Grupo.asignarHora(e);
     });
     
     Grupo.body.delegate('.desasignar-hora', 'dblclick', function (e) {
     Grupo.desasignarHora($(this), e);
     });
     
     Grupo.init();
     //*/

});

Vue.component("multiselect", window.VueMultiselect.default);
new Vue({
    el: '#grupoVUE',
    data: {
        grupoURL: '',
        tipoGpo: {},
        tipoHorarios: [],
        dias: [],
        horas: [],
        horarioRegular: [],
        horarioGpo: [],
        ciclo: {},
        paginationGpo: {'total-items': 0, 'items-per-page': 12, 'max-size': 3, 'boundary-link-numbers': true},
        grupoActivo: {},
        configCloneCiclo: VUE_MODAL.structFormAjax({
            id: 'modalCloneCiclo',
            title: 'Copiar Ciclo',
            header: true
        }),
        configEditGrupo: VUE_MODAL.structFormAjax({
            id: 'modalEditGrupo',
            title: 'Editar Grupo Horas',
            header: true,
            form: 'formEditGrupo',
            verPaleta: false,
            verLetras: false,
        }),
        grupoHorasForm: {},
        colors: ['#1abc9c', '#e8f8f5', '#d1f2eb', '#a3e4d7', '#76d7c4', '#48c9b0', '#1abc9c', '#17a589', '#148f77', '#117864', '#0e6251', '#16a085', '#e8f6f3', '#d0ece7', '#a2d9ce', '#73c6b6', '#45b39d', '#16a085', '#138d75', '#117a65', '#0e6655', '#0b5345', '#2ecc71', '#eafaf1', '#d5f5e3', '#abebc6', '#82e0aa', '#58d68d', '#2ecc71', '#28b463', '#239b56', '#1d8348', '#186a3b', '#27ae60', '#e9f7ef', '#d4efdf', '#a9dfbf', '#7dcea0', '#52be80', '#27ae60', '#229954', '#1e8449', '#196f3d', '#145a32', '#3498db', '#ebf5fb', '#d6eaf8', '#aed6f1', '#85c1e9', '#5dade2', '#3498db', '#2e86c1', '#2874a6', '#21618c', '#1b4f72', '#2980b9', '#eaf2f8', '#d4e6f1', '#a9cce3', '#7fb3d5', '#5499c7', '#2980b9', '#2471a3', '#1f618d', '#1a5276', '#154360', '#9b59b6', '#f5eef8', '#ebdef0', '#d7bde2', '#c39bd3', '#af7ac5', '#9b59b6', '#884ea0', '#76448a', '#633974', '#512e5f', '#8e44ad', '#f4ecf7', '#e8daef', '#d2b4de', '#bb8fce', '#a569bd', '#8e44ad', '#7d3c98', '#6c3483', '#5b2c6f', '#4a235a', '#34495e', '#ebedef', '#d6dbdf', '#aeb6bf', '#85929e', '#5d6d7e', '#34495e', '#2e4053', '#283747', '#212f3c', '#1b2631', '#2c3e50', '#eaecee', '#d5d8dc', '#abb2b9', '#808b96', '#566573', '#2c3e50', '#273746', '#212f3d', '#1c2833', '#17202a', '#f1c40f', '#fef9e7', '#fcf3cf', '#f9e79f', '#f7dc6f', '#f4d03f', '#f1c40f', '#d4ac0d', '#b7950b', '#9a7d0a', '#7d6608', '#f39c12', '#fef5e7', '#fdebd0', '#fad7a0', '#f8c471', '#f5b041', '#f39c12', '#d68910', '#b9770e', '#9c640c', '#7e5109', '#e67e22', '#fdf2e9', '#fae5d3', '#f5cba7', '#f0b27a', '#eb984e', '#e67e22', '#ca6f1e', '#af601a', '#935116', '#784212', '#d35400', '#fbeee6', '#f6ddcc', '#edbb99', '#e59866', '#dc7633', '#d35400', '#ba4a00', '#a04000', '#873600', '#6e2c00', '#e74c3c', '#fdedec', '#fadbd8', '#f5b7b1', '#f1948a', '#ec7063', '#e74c3c', '#cb4335', '#b03a2e', '#943126', '#78281f', '#c0392b', '#f9ebea', '#f2d7d5', '#e6b0aa', '#d98880', '#cd6155', '#c0392b', '#a93226', '#922b21', '#7b241c', '#641e16', '#ecf0f1', '#fdfefe', '#fbfcfc', '#f7f9f9', '#f4f6f7', '#f0f3f4', '#ecf0f1', '#d0d3d4', '#b3b6b7', '#979a9a', '#7b7d7d', '#bdc3c7', '#f8f9f9', '#f2f3f4', '#e5e7e9', '#d7dbdd', '#cacfd2', '#bdc3c7', '#a6acaf', '#909497', '#797d7f', '#626567', '#95a5a6', '#f4f6f6', '#eaeded', '#d5dbdb', '#bfc9ca', '#aab7b8', '#95a5a6', '#839192', '#717d7e', '#5f6a6a', '#4d5656', '#7f8c8d', '#f2f4f4', '#e5e8e8', '#ccd1d1', '#b2babb', '#99a3a4', '#7f8c8d', '#707b7c', '#616a6b', '#515a5a', '#424949'],
        letras: ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W'],

    },
    created() {
        this.tipoGpo = JSON.parse(tipoGpoJson);
        this.dias = JSON.parse(diasJson);
        this.horas = JSON.parse(horasJson);
        this.horarioRegular = JSON.parse(horarioRegularJson);
        this.tipoHorarios = JSON.parse(tipoHorariosJson);
        this.grupoURL = APP.url(rutaModulo + '/list?idTipoGrupo=' + this.tipoGpo.id);
    },
    mounted() {
        let $vue = this;
        $vue.$refs.raptorGrupo.loadRemoteData();
    },
    methods: {
        styleHdia(dia, hora) {
            let $vue = this;
            for (var i = 0; i < $vue.horarioGpo.length; i++) {
                if ($vue.horarioGpo[i].hora.id == hora.id && $vue.horarioGpo[i].dia.id == dia.id) {
                    return "border-color:#600D63; background-color:#DCDFE3;color:#000000;"
                }
            }
            return "border-color:#DFE7EE; background-color:#FFFFFF;color:#E40DEB;"
        },
        styleConteHdia(dia, hora) {
            let $vue = this;
            for (var i = 0; i < $vue.horarioGpo.length; i++) {
                if ($vue.horarioGpo[i].hora.id == hora.id && $vue.horarioGpo[i].dia.id == dia.id) {
                    return "";
                }
            }
            return "color:#E40DEB;";
        },
        conteHdia(dia, hora) {
            let $vue = this;
            for (var i = 0; i < $vue.horarioGpo.length; i++) {
                if ($vue.horarioGpo[i].hora.id == hora.id && $vue.horarioGpo[i].dia.id == dia.id) {
                    return $vue.horarioGpo[i].grupoHorario.codigo;
                }
            }
            for (var i = 0; i < $vue.horarioRegular.length; i++) {
                if ($vue.horarioRegular[i].hora.id == hora.id && $vue.horarioRegular[i].dia.id == dia.id) {
                    return $vue.horarioRegular[i].grupoHorario.codigo;
                }
            }
            return "";
        },
        verHorario(item) {
            let $vue = this;
            $vue.grupoActivo = item;
            
            if ($vue.grupoActivo == undefined) {
                return;
            }
            if ($vue.grupoActivo.id == undefined) {
                return;
            }

            $.ajax({
                url: APP.url(rutaModulo + '/horario'),
                type: 'POST',
                async: false,
                data: {id: item.id},
                success: function (response) {
                    if (response.success) {
                        $vue.horarioRegular = response.data.horarioRegular;
                        $vue.horarioGpo = response.data.horarioGpo;
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });


        },
        styleBorder(item) {
            let $vue = this;
            if (item.id == $vue.grupoActivo.id) {
                return "background-color:gray;";
            }
            return "border-color:" + item.color + ";";
        },
        classHoras(item) {
            if (item.horas == 0) {
                return "label-danger";
            }
            return "label-success";
        },
        asignarHora(dia, hora) {
            let $vue = this;
            if ($vue.grupoActivo == null) {
                return;
            }
            if (!$vue.validarDia(dia, hora)) {
                notify("No puede haber horario por intervalos", "error");
                return;
            }

            $vue.data = {};
            $vue.data.dia = dia;
            $vue.data.hora = hora;
            $vue.data.grupoHorario = $vue.grupoActivo;

            axios.post('/' + rutaModulo + '/asignarHora', $vue.data)
                    .then(response => {
                        if (response.data.success) {
                            $vue.verHorario($vue.grupoActivo);
                            $vue.$refs.raptorGrupo.loadRemoteData();
                            notify(response.data.message, "success");
                        } else {
                            notify(response.data.message, "error");
                        }
                    })
                    .catch(function (error) {
                        vue.generando = false;
                        notify(MESSAGES.errorComunicacion, "error");
                    });
        },
        desasignarHora(dia, hora) {
            let $vue = this;

            if ($vue.grupoActivo == null) {
                return;
            }
            $vue.data = {};
            $vue.data.id = $vue.conterObjHdia(dia, hora).id;
            axios.post('/' + rutaModulo + '/desasignarHora', $vue.data)
                    .then(response => {
                        if (response.data.success) {
                            $vue.verHorario($vue.grupoActivo);
                            $vue.$refs.raptorGrupo.loadRemoteData();
                            notify(response.data.message, "success");
                        } else {
                            notify(response.data.message, "error");
                        }
                    })
                    .catch(function (error) {
                        notify(MESSAGES.errorComunicacion, "error");
                    });
        },
        conterObjHdia(dia, hora) {
            let $vue = this;
            for (var i = 0; i < $vue.horarioGpo.length; i++) {
                if ($vue.horarioGpo[i].hora.id == hora.id && $vue.horarioGpo[i].dia.id == dia.id) {
                    return $vue.horarioGpo[i];
                }
            }
            return null;
        },
        validarDia(dia, hora) {
            let $vue = this;
            var res = false;
            var obj = [];
            for (var i = 0; i < $vue.horarioGpo.length; i++) {
                if ($vue.horarioGpo[i].dia.id == dia.id) {
                    obj.push($vue.horarioGpo[i]);
                }
            }

            if (obj.length == 0) {
                res = true;
            } else {
                var temp = parseInt(hora.codigo);
                obj.forEach(function (item) {
                    var horNext = parseInt(item.hora.codigo) + 100;
                    var horAnt = parseInt(item.hora.codigo) - 100;
                    if (temp == horAnt || temp == horNext) {
                        res = true;
                    }
                });
            }
            return res;
        },
        clonarCiclo() {
            let $vue = this;
            $vue.ciclo = {id: null};
            $vue.$refs.modalCloneCiclo.open();
        },
        saveCloneCiclo() {
            let $vue = this;
            console.log($vue.ciclo);

            axios.post('/' + rutaModulo + '/clonarGrupos', $vue.ciclo)
                    .then(response => {
                        if (response.data.success) {
                            $vue.$refs.modalCloneCiclo.close();
                            $vue.$refs.raptorGrupo.loadRemoteData();
                            notify(response.data.message, "success");
                        } else {
                            notify(response.data.message, "error");
                        }
                    })
                    .catch(function (error) {
                        notify(MESSAGES.errorComunicacion, "error");
                    });

        },
        verEditarGrupo(item) {
            let $vue = this;
            $vue.grupoHorasForm = Object.assign({}, item);
            $vue.configEditGrupo.verPaleta = false;
            $vue.configEditGrupo.verLetras = false;
            $vue.grupoHorasForm.conHorarioEnum = {};
            for (var i = 0; i < $vue.tipoHorarios.length; i++) {
                if ($vue.grupoHorasForm.conHorario == $vue.tipoHorarios[i].name) {
                    $vue.grupoHorasForm.conHorarioEnum = $vue.tipoHorarios[i];
                }
            }
            $vue.$refs.modalEditGrupo.open();
        },
        saveEditGrupo() {
            let $vue = this;
            let form = $("#" + $vue.configEditGrupo.form);
            if (!form.parsley().validate()) {
                return;
            }

            $vue.grupoHorasForm.conHorario = $vue.grupoHorasForm.conHorarioEnum.name;
            $vue.$refs.modalEditGrupo.beginProcessing();
            axios.post('/' + rutaModulo + '/save', $vue.grupoHorasForm)
                    .then(response => {
                        $vue.$refs.modalEditGrupo.confirmReaction(response.data.success);
                        if (response.data.success) {
                            $vue.verHorario($vue.grupoActivo);
                            $vue.$refs.raptorGrupo.loadRemoteData();
                            notify(response.data.message, "success");
                        } else {
                            notify(response.data.message, "error");
                        }
                    })
                    .catch(function (error) {
                        notify(MESSAGES.errorComunicacion, "error");
                    });
        },
        verColores() {
            let $vue = this;
            $vue.configEditGrupo.verPaleta = $vue.configEditGrupo.verPaleta ? false : true;
            $vue.configEditGrupo.verLetras = false;
        },
        findBgColor(n, m) {
            let $vue = this;
            let nn = parseInt(n);
            let mm = parseInt(m);
            let idx = nn + 11 * (mm - 1) - 1;

            return "background-color:" + $vue.colors[idx] + ";";
        },
        setColor(n, m) {
            let $vue = this;
            let nn = parseInt(n);
            let mm = parseInt(m);
            let idx = nn + 11 * (mm - 1) - 1;
            $vue.grupoHorasForm.color = $vue.colors[idx];
        },
        getLetra(n, m) {
            let $vue = this;
            let nn = parseInt(n);
            let mm = parseInt(m);
            let idx = mm + 6 * (nn - 1) - 1;
            return $vue.letras[idx];
        },
        setLetra(n, m) {
            let $vue = this;
            let nn = parseInt(n);
            let mm = parseInt(m);
            let idx = mm + 6 * (nn - 1) - 1;
            $vue.grupoHorasForm.letra = $vue.letras[idx];
        },
        styleLetra(n, m) {
            let $vue = this;
            let nn = parseInt(n);
            let mm = parseInt(m);
            let idx = mm + 6 * (nn - 1) - 1;
            let letra = $vue.letras[idx];
            if ($vue.grupoHorasForm.letra == letra) {
                return "background-color: green; color: white;"
            }
            return "";

        },
        verAbc() {
            let $vue = this;
            $vue.configEditGrupo.verLetras = $vue.configEditGrupo.verLetras ? false : true;
            $vue.configEditGrupo.verPaleta = false;
        },
        revisar(tipo, ofi, campo) {
            let $vue = this;
            if (tipo == 'CODIGO') {
                ofi[campo] = VUE.revisarCodigo(ofi[campo]);
            } else if (tipo == 'EMAIL') {
                ofi[campo] = VUE.revisarEmail(ofi[campo]);
            } else if (tipo == 'NOMBRE') {
                ofi[campo] = VUE.revisarNombreObjeto(ofi[campo]);
            } else if (tipo == 'ANEXOS') {
                ofi[campo] = VUE.revisarAnexos(ofi[campo]);
            } else if (tipo == 'TELEFONOS') {
                ofi[campo] = VUE.revisarTelefonos(ofi[campo]);
            }
        }
    }
});
