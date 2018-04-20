Vue.component("multiselect", window.VueMultiselect.default);
new Vue({
    el: '#main',
    data: {
        tipos: JSON.parse(tiposJson),
        tipoConstancia: {},
        listTipoDocumento: [],
        isNew: true,
        oficinas: [],
        tiposOficina: [],
        ordenFirma: 1,
        firmasDocumento: [{id: null, orden: 1, tipoOficina: {id: null}, oficina: {id: null}}],
        addTipoConstanciaModal: {
            id: 'modalAddTipoConstancia',
            header: true,
            title: 'Nuevo Tipo Constancia',
            okbtn: 'Agregar Tipo Constancia'
        },
    },
    mounted: function() {
        let vue = this;
        $global.$on("updateTipo", function(tipoConstancia) {
            vue.updateTipo(tipoConstancia);
        });
        $global.$on("eliminar", function(tipoConstancia) {
            vue.eliminar(tipoConstancia);
        });
        $("[name='tipo']").select2({minimumResultsForSearch: -1});
    },
    methods: {
        eliminar: function(tipoConstancia) {
            var vue = this;
            bootbox.confirm({
                message: '¿Seguro que desea eliminar el tipo  de constancia?',
                buttons: {
                    confirm: {label: 'Si, eliminar', className: "btn-danger"},
                    cancel: {label: 'Salir', className: "btn-link"}
                },
                callback: function(result) {
                    if (result) {
                        $.ajax({
                            method: 'POST',
                            url: APP.url('tramite/tipoconstancia/delete'),
                            data: {id: tipoConstancia.id},
                            success: function(response) {
                                if (response.success) {
                                    notify(response.message, 'info');
                                    $dynatable.process();
                                } else {
                                    notify(response.message, 'error');
                                }
                            }, error: function() {
                                notify(MESSAGES.errorComunicacion, "error");
                            }
                        });
                    }
                }
            });
        },
        updateTipo: function(tipoConstancia) {
            let vue = this;
            vue.tipoConstancia = tipoConstancia;
            vue.isNew = false;
            vue.$refs.modalAddTipoConstancia.open();
        },
        nuevo: function() {
            let vue = this;
            vue.tipoConstancia = {};
            vue.isNew = true;
            vue.$refs.modalAddTipoConstancia.open();

            $(".oficina").select2(vue.selectOficina()).on('change.select2', function(e) {
                let self = $(e.currentTarget);
                let inx = self.attr("rev");
                let firmaDocumento = vue.firmasDocumento[inx];
                firmaDocumento.oficina = e.added;
            });

            $(".tipoOficina").select2(vue.selectTipoOficina()).on('change.select2', function(e) {
                let self = $(e.currentTarget);
                let inx = self.attr("rev");
                let firmaDocumento = vue.firmasDocumento[inx];
                firmaDocumento.tipoOficina = e.added;
            });

        },
        save: function(e) {
            var self = $(e.currentTarget);
            self.btnDisabled();
            $(".mx-input").attr("required", true);
            if (!$("#formTipoConstancia").parsley().validate() == true) {
                self.btnEnable();
                return;
            }
            self.btnEnable();
            let vue = this;
            vue.temp = {};
            vue.temp.nombre = vue.tipoConstancia.nombre;
            vue.temp.costoCiclo = vue.tipoConstancia.costoCiclo == true ? 1 : 0;
            vue.temp.tipo = vue.tipoConstancia.tipo.name;
            $.ajax({
                method: 'POST',
                url: APP.url('tramite/tipoconstancia/save'),
                contentType: "application/json",
                data: JSON.stringify(vue.temp),
                success: function(response) {
                    if (response.success) {
                        notify(response.message, 'info');
                        vue.$refs.modalAddTipoConstancia.close();
                        $dynatable.process();
                    } else {
                        notify(response.message, 'error');
                    }
                }, error: function() {
                    vue.$refs.modalAddTipoConstancia.close();
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        agregar: function(e) {
            let self = $(e.currentTarget);
            self.btnDisabled();
            let vue = this;
            let orden = vue.ordenFirma + 1;
            vue.ordenFirma++;
            vue.firmasDocumento.push({orden: orden, tipoOficina: {}, oficina: {}});
            setTimeout(function() {

                $(".oficina").select2(vue.selectOficina()).on('change.select2', function(e) {
                    let self = $(e.currentTarget);
                    let inx = self.attr("rev");
                    let firmaDocumento = vue.firmasDocumento[inx];
                    firmaDocumento.oficina = e.added;
                });

                $(".tipoOficina").select2(vue.selectTipoOficina()).on('change.select2', function(e) {
                    let self = $(e.currentTarget);
                    let inx = self.attr("rev");
                    let firmaDocumento = vue.firmasDocumento[inx];
                    firmaDocumento.tipoOficina = e.added;
                });

                self.btnEnable();
            }, 200);
        },
        eliminarFirma: function(firma) {
            let vue = this;
            vue.firmasDocumento.splice(vue.firmasDocumento.indexOf(firma), 1);
        },
        selectOficina(self) {
            var vue = this;
            return {
                allowClear: true,
                placeholder: "Seleccione un oficina",
                minimumInputLength: 1,
                ajax: {
                    url: APP.url('tramite/tipoconstancia/allOficina'),
                    dataType: 'json',
                    type: 'post',
                    data: function(term, page) {
                        return {nombre: term, page: page};
                    },
                    results: function(response, page) {
                        return {results: response.data};
                    }
                },
                initSelection: function(element, callback) {
                    if (element.val() != "") {
                        var inx = element.attr("rev");
                        console.log(inx);
                        let firmaDocumento = vue.firmasDocumento[inx];
                        callback(firmaDocumento.oficina);
//                        var datos = {
//                            id: element.val(),
//                            nombre: element.attr("rel")
//                        };
//                        callback(datos);
                    }
                },
                formatResult: function(info) {
                    return  info.nombre;
                },
                formatSelection: function(info) {
                    return  info.nombre;
                },
                escapeMarkup: function(m) {
                    return m;
                }
            };
        },
        selectTipoOficina(self) {
            var vue = this;
            return {
                allowClear: true,
                placeholder: "Seleccione un tipo de oficina",
                minimumInputLength: 1,
                ajax: {
                    url: APP.url('tramite/tipoconstancia/allTipoOficina'),
                    dataType: 'json',
                    type: 'post',
                    data: function(term, page) {
                        return {nombre: term, page: page};
                    },
                    results: function(response, page) {
                        return {results: response.data};
                    }
                },
                initSelection: function(element, callback) {
                    if (element.val() != "") {
                        var inx = element.attr("rev");
                        console.log(inx);
                        let firmaDocumento = vue.firmasDocumento[inx];
                        callback(firmaDocumento.tipoOficina);
//                        var datos = {
//                            id: element.val(),
//                            nombre: element.attr("rel")
//                        };
//                        callback(datos);
                    }
                },
                formatResult: function(info) {
                    return   info.nombre;
                },
                formatSelection: function(info) {
                    return   info.nombre;
                },
                escapeMarkup: function(m) {
                    return m;
                }
            };
        },
    }
});
