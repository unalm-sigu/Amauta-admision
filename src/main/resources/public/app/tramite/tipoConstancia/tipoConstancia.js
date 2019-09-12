new Vue({
    el: '#main',
    data: {
        tipos: JSON.parse(tiposJson),
        tipoConstancia: {tipo: {}},
        listTipoDocumento: [],
        copia: '',
        oficinas: [],
        tiposOficina: [],
        firmasDocumento: [{orden: 1, tipoOficina: {}, oficina: {}}],
        addTipoConstanciaModal: VUE_MODAL.structFormAjax({
            id: 'modalAddTipoConstancia',
            header: true,
            title: 'Nuevo Tipo Constancia',
            okbtn: 'Agregar Tipo Constancia',
            modalsize: 'modal-lg',
            modalscroll: 'modal-scroll-500'
            
        }),
    },
    computed: {
        orderedFirmasDocumento: function() {
            return _.orderBy(this.firmasDocumento, 'orden');
        }
    },
    mounted: function() {
        let vue = this;
        $global.$on("updateTipo", function(tipoConstancia) {
            vue.updateTipo(tipoConstancia);
        });
        $global.$on("eliminar", function(tipoConstancia) {
            vue.eliminar(tipoConstancia);
        });
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
            vue.tipoConstancia = {tipo: {}};
            vue.firmasDocumento = [{orden: 1, tipoOficina: {}, oficina: {}}];
            $("#formTipoConstancia").parsley().destroy();
            $.ajax({
                method: 'POST',
                url: APP.url('tramite/tipoconstancia/update'),
                data: {id: tipoConstancia.id},
                success: function(response) {
                    if (response.success) {
                        vue.tipoConstancia = response.data;
                        if (response.data.firmasDocumento.length > 0) {
                            vue.firmasDocumento = response.data.firmasDocumento;
                            console.log(response.data.firmasDocumento);
                        }
                    } else {
                        notify(response.message, 'error');
                    }
                }, error: function() {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
            vue.$refs.modalAddTipoConstancia.open();
            setTimeout(function() {
                vue.updateSelect2();
            }, 100);
        },
        updateSelect2: function() {
            let vue = this;
            try {
                $(".oficina").select2('destroy');
                $(".tipoOficina").select2('destroy');
            } catch (e) {
                console.log(e.toString());
            }

            $(".oficina").select2(vue.selectOficina()).on('change.select2', function(e) {
                let self = $(e.currentTarget);
                let orden = parseInt(self.attr("rev"));
                let firmaDocumento = vue.firmasDocumento.find(item => item.orden === orden);
                firmaDocumento.oficina = e.added;
                firmaDocumento.tipoOficina = {};
                setTimeout(function() {
                    vue.updateSelect2();
                }, 100);
            });
            $(".tipoOficina").select2(vue.selectTipoOficina()).on('change.select2', function(e) {
                let self = $(e.currentTarget);
                let orden = parseInt(self.attr("rev"));
                let firmaDocumento = vue.firmasDocumento.find(item => item.orden === orden);
                firmaDocumento.tipoOficina = e.added;
                firmaDocumento.oficina = {};
                setTimeout(function() {
                    vue.updateSelect2();
                }, 100);
            });
        },
        nuevo: function() {
            let vue = this;
            vue.tipoConstancia = {tipo: {}};
            vue.firmasDocumento = [{orden: 1, tipoOficina: {}, oficina: {}}];
            vue.$refs.modalAddTipoConstancia.open();
            setTimeout(function() {
                vue.updateSelect2();
            }, 100);
            $("[name='tipo']").select2('val', '');
        },
        save: function(e) {
            let vue = this;
            var self = $(e.currentTarget);
            self.btnDisabled();
            $(".mx-input").attr("required", true);
            if (!$("#formTipoConstancia").parsley().validate() == true) {
                self.btnEnable();
                return;
            }
            $.ajax({
                method: 'POST',
                url: APP.url('tramite/tipoconstancia/save'),
                data: $("#formTipoConstancia").serialize(),
                success: function(response) {
                    if (response.success) {
                        notify(response.message, 'info');
                        vue.$refs.modalAddTipoConstancia.close();
                        $dynatable.process();
                    } else {
                        notify(response.message, 'error');
                    }
                    self.btnEnable();
                }, error: function() {
                    vue.$refs.modalAddTipoConstancia.close();
                    notify(MESSAGES.errorComunicacion, "error");
                    self.btnEnable();
                }
            });
        },
        agregar: function(e) {
            let self = $(e.currentTarget);
            self.btnDisabled();
            let vue = this;
            let orden = vue.firmasDocumento.length + 1;
            vue.firmasDocumento.push({orden: orden, tipoOficina: {}, oficina: {}});
            setTimeout(function() {
                vue.updateSelect2();
                self.btnEnable();
            }, 100);
        },
        eliminarFirma: function(firma) {
            let vue = this;
            if (vue.firmasDocumento.length < 2) {
                notify("Debe haber una firma como mínimo", 'error');
                return;
            }
            let backOrder = parseInt(firma.orden);
            let maxOrder = parseInt(vue.firmasDocumento.length);
            vue.$delete(vue.firmasDocumento, vue.firmasDocumento.indexOf(firma));
            vue.reOrder(backOrder, maxOrder);
            setTimeout(function() {
                vue.updateSelect2();
            }, 100);
        },
        selectOficina: function() {
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
                        var datos = {
                            id: element.val(),
                            nombre: element.attr("rel")
                        };
                        callback(datos);
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
        selectTipoOficina: function() {
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
                        var datos = {
                            id: element.val(),
                            nombre: element.attr("rel")
                        };
                        callback(datos);
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
        upFirma: function(firma) {
            let vue = this;
            if (firma.orden < 2) {
                return;
            }
            let oldOrder = parseInt(firma.orden);
            let newOrder = parseInt(firma.orden - 1);
            let firmaDocumento = vue.firmasDocumento.find(item => item.orden === newOrder);
            firmaDocumento.orden = oldOrder;
            firma.orden = newOrder;
            setTimeout(function() {
                vue.updateSelect2();
            }, 50);
        },
        downFirma: function(firma) {
            let vue = this;
            if (firma.orden >= vue.firmasDocumento.length) {
                return
            }
            let oldOrder = parseInt(firma.orden);
            let newOrder = parseInt(firma.orden + 1);
            let firmaDocumento = vue.firmasDocumento.find(item => item.orden === newOrder);
            firmaDocumento.orden = oldOrder;
            firma.orden = newOrder;
            setTimeout(function() {
                vue.updateSelect2();
            }, 50);
        },
        reOrder: function(backOrder, max) {
            let vue = this;
            if (max <= backOrder) {
                return;
            }
            for (var i = backOrder; i <= max; i++) {
                let firmaDocumento = vue.firmasDocumento.find(item => item.orden === (i + 1));
                if (firmaDocumento) {
                    firmaDocumento.orden = i;
                }
            }
        }
    }
});
