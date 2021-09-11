<template>
    <div>


        <modal-vik ref="modalAddTipoConstancia"
                   v-bind="addTipoConstanciaModal"
                   v-bind:okaction="save">

            <div slot="body" >

                <form id="formTipoConstancia" class="form-horizontal"  data-parsley-validate="true">

                    <input   v-model="tipoConstancia.id" type="hidden" name="id"  />

                    <div class='form-group row'>
                        <label class="col-sm-3 control-label">Nombre:</label>
                        <div class="col-sm-6">
                            <input name="nombre"  required="true" v-model="tipoConstancia.nombre" type="text" class="form-control " />
                        </div>
                    </div>

                    <div class='form-group'>
                        <label class="col-sm-3 control-label">Tipo:</label>
                        <div class="col-sm-6">
                            <select name="tipo" class="form-control"   required="true"  v-model="tipoConstancia.tipo">
                                <option v-for="tipo in tipos"   v-bind:value="tipo.name" v-text="tipo.value"  ></option>
                            </select>
                        </div>
                    </div>


                    <div class='form-group' >
                        <label class="col-sm-3 control-label">Costo ciclo:</label>
                        <div class="col-sm-9">
                            <label class="switch">
                                <input type="checkbox" name="costoCiclo" value="1"  v-model="tipoConstancia.costoCiclo" ></input>
                                <span></span>
                            </label>
                        </div>
                    </div>

                    <div class='form-group' >
                        <label class="col-sm-3 control-label">Requiere Foto:</label>
                        <div class="col-sm-9">
                            <label class="switch">
                                <input type="checkbox" name="requiereFoto" value="1"  v-model="tipoConstancia.requiereFoto" ></input>
                                <span></span>
                            </label>
                        </div>
                    </div>

                    <div class='form-group' >
                        <label class="col-sm-3 control-label">Solo Pregrado:</label>
                        <div class="col-sm-9">
                            <label class="switch">
                                <input type="checkbox" name="requierePregrado" value="1"  v-model="tipoConstancia.requierePregrado" ></input>
                                <span></span>
                            </label>
                        </div>
                    </div>

                    <div class='form-group' >
                        <label class="col-sm-3 control-label">Solo Posgrado:</label>
                        <div class="col-sm-9">
                            <label class="switch">
                                <input type="checkbox" name="requierePosgrado" value="1"  v-model="tipoConstancia.requierePosgrado" ></input>
                                <span></span>
                            </label>
                        </div>
                    </div>

                    <div class='form-group' >
                        <label class="col-sm-3 control-label">Solo Egresado:</label>
                        <div class="col-sm-9">
                            <label class="switch">
                                <input type="checkbox" name="requiereEgresado" value="1"  v-model="tipoConstancia.requiereEgresado" ></input>
                                <span></span>
                            </label>
                        </div>
                    </div>

                    <div class='form-group' >
                        <label class="col-sm-3 control-label">Codigo:</label>
                        <div class="col-sm-6">
                            <input  name="codigo"  required="true" v-model="tipoConstancia.codigo" type="text" class="form-control " /> 
                            <!--v-bind:disabled="tipoConstancia.id != null"-->
                        </div>
                    </div>

                    <div class="table-responsive">

                        <table class="table table-condensed">
                            <thead>
                                <tr>
                                    <th></th>
                                    <th>Orden</th>
                                    <th>Oficina</th>
                                    <th>Tipo Oficina</th>
                                    <th></th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr   v-for="(firma, index)  in orderedFirmasDocumento">

                                    <td class='v-middle'>
                                        <i class="fa fa-arrow-up block pointer" v-on:click="upFirma(firma)"  aria-hidden="true"></i>
                                        <i class="fa fa-arrow-down block pointer" v-on:click="downFirma(firma)"  aria-hidden="true"></i>
                                    </td>

                                    <td class="v-middle text-center" >

                                        <input
                                            v-bind:name=" 'configuracionFirmaDocumento['+index+'].orden' "
                                            type="hidden"
                                            v-model="firma.orden" />

                                        <p v-text="firma.orden"></p>

                                    </td>

                                    <td class="v-middle col-xs-6" >

                                        <input
                                            v-bind:name=" 'configuracionFirmaDocumento['+index+'].oficina.id' "
                                            type="text"
                                            v-model="firma.oficina.id"
                                            v-bind:rev="firma.orden"
                                            v-bind:rel="firma.oficina.nombre"
                                            class="form-control oficina" />

                                    </td>

                                    <td class="v-middle col-xs-6" >

                                        <input
                                            v-bind:name=" 'configuracionFirmaDocumento['+index+'].tipoOficina.id' "
                                            type="text"
                                            v-model="firma.tipoOficina.id"
                                            v-bind:rev="firma.orden"
                                            v-bind:rel="firma.tipoOficina.nombre"
                                            class="form-control tipoOficina" />

                                    </td>

                                    <td class="v-middle">
                                        <div class="dropdown actions">
                                            <a class="dropdown-toggle" data-toggle="dropdown">
                                                <i class="fa fa-cog pointer"></i>
                                            </a>
                                            <ul class="dropdown-menu pull-right" >
                                                <li><a href="#"   v-on:click.prevent="eliminarFirma(firma)">Eliminar</a></li>
                                            </ul>
                                        </div>
                                    </td>

                                </tr>
                            </tbody>
                        </table>
                    </div>

                    <div class='form-group' >
                        <div class="text-center">
                            <button type="button" class="btn btn-info btn-sm" v-on:click.prevent="agregar($event)">Agregar firmante</button>
                        </div>
                    </div>

                </form>
            </div>
        </modal-vik>


    </div>
</template>

<script>
    module.exports = {
        data() {
            return {
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
            };
        },
        mounted: function () {
            let $vue = this;
        },
        computed: {
            orderedFirmasDocumento: function () {
                return this.firmasDocumento;
            }
        },
        methods: {

            updateTipo: function (tipoConstancia) {
                let vue = this;
                vue.tipoConstancia = {tipo: {}};
                vue.firmasDocumento = [{orden: 1, tipoOficina: {}, oficina: {}}];
                $("#formTipoConstancia").parsley().destroy();
                $.ajax({
                    method: 'POST',
                    url: APP.url('tramite/tipoconstancia/find/' + tipoConstancia.id),
                    success: function (response) {
                        if (response.success) {
                            vue.tipoConstancia = response.data;
                            if (response.data.firmasDocumento.length > 0) {
                                vue.firmasDocumento = response.data.firmasDocumento;
                                console.log(response.data.firmasDocumento);
                            }
                        } else {
                            notify(response.message, 'error');
                        }
                    }, error: function () {
                        notify(Messages.errorComunicacion, "error");
                    }
                });
                vue.$refs.modalAddTipoConstancia.open();
                setTimeout(function () {
                    vue.updateSelect2();
                }, 100);
            },
            updateSelect2: function () {
                let vue = this;
                try {
                    $(".oficina").select2('destroy');
                    $(".tipoOficina").select2('destroy');
                } catch (e) {
                    console.log(e.toString());
                }

                $(".oficina").select2(vue.selectOficina()).on('change.select2', function (e) {
                    let self = $(e.currentTarget);
                    let orden = parseInt(self.attr("rev"));
                    let firmaDocumento = vue.firmasDocumento.find(item => item.orden === orden);
                    firmaDocumento.oficina = e.added;
                    firmaDocumento.tipoOficina = {};
                    setTimeout(function () {
                        vue.updateSelect2();
                    }, 100);
                });
                $(".tipoOficina").select2(vue.selectTipoOficina()).on('change.select2', function (e) {
                    let self = $(e.currentTarget);
                    let orden = parseInt(self.attr("rev"));
                    let firmaDocumento = vue.firmasDocumento.find(item => item.orden === orden);
                    firmaDocumento.tipoOficina = e.added;
                    firmaDocumento.oficina = {};
                    setTimeout(function () {
                        vue.updateSelect2();
                    }, 100);
                });
            },
            nuevo: function () {
                let vue = this;
                vue.tipoConstancia = {tipo: {}};
                vue.firmasDocumento = [{orden: 1, tipoOficina: {}, oficina: {}}];
                vue.$refs.modalAddTipoConstancia.open();
                setTimeout(function () {
                    vue.updateSelect2();
                }, 100);
                $("[name='tipo']").select2('val', '');
            },
            save: function (e) {
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
                    success: function (response) {
                        if (response.success) {
                            notify(response.message, 'info');
                            vue.$refs.modalAddTipoConstancia.close();
                            vue.$parent.reload();
                        } else {
                            notify(response.message, 'error');
                        }
                        self.btnEnable();
                    }, error: function () {
                        vue.$refs.modalAddTipoConstancia.close();
                        notify(Messages.errorComunicacion, "error");
                        self.btnEnable();
                    }
                });
            },
            agregar: function (e) {
                let self = $(e.currentTarget);
                self.btnDisabled();
                let vue = this;
                let orden = vue.firmasDocumento.length + 1;
                vue.firmasDocumento.push({orden: orden, tipoOficina: {}, oficina: {}});
                setTimeout(function () {
                    vue.updateSelect2();
                    self.btnEnable();
                }, 100);
            },
            eliminarFirma: function (firma) {
                let vue = this;
                if (vue.firmasDocumento.length < 2) {
                    notify("Debe haber una firma como mínimo", 'error');
                    return;
                }
                let backOrder = parseInt(firma.orden);
                let maxOrder = parseInt(vue.firmasDocumento.length);
                vue.$delete(vue.firmasDocumento, vue.firmasDocumento.indexOf(firma));
                vue.reOrder(backOrder, maxOrder);
                setTimeout(function () {
                    vue.updateSelect2();
                }, 100);
            },
            selectOficina: function () {
                return {
                    allowClear: true,
                    placeholder: "Seleccione un oficina",
                    minimumInputLength: 1,
                    ajax: {
                        url: APP.url('tramite/tipoconstancia/allOficina'),
                        dataType: 'json',
                        type: 'post',
                        data: function (term, page) {
                            return {nombre: term, page: page};
                        },
                        results: function (response, page) {
                            return {results: response.data};
                        }
                    },
                    initSelection: function (element, callback) {
                        if (element.val() != "") {
                            var datos = {
                                id: element.val(),
                                nombre: element.attr("rel")
                            };
                            callback(datos);
                        }
                    },
                    formatResult: function (info) {
                        return  info.nombre;
                    },
                    formatSelection: function (info) {
                        return  info.nombre;
                    },
                    escapeMarkup: function (m) {
                        return m;
                    }
                };
            },
            selectTipoOficina: function () {
                return {
                    allowClear: true,
                    placeholder: "Seleccione un tipo de oficina",
                    minimumInputLength: 1,
                    ajax: {
                        url: APP.url('tramite/tipoconstancia/allTipoOficina'),
                        dataType: 'json',
                        type: 'post',
                        data: function (term, page) {
                            return {nombre: term, page: page};
                        },
                        results: function (response, page) {
                            return {results: response.data};
                        }
                    },
                    initSelection: function (element, callback) {
                        if (element.val() != "") {
                            var datos = {
                                id: element.val(),
                                nombre: element.attr("rel")
                            };
                            callback(datos);
                        }
                    },
                    formatResult: function (info) {
                        return   info.nombre;
                    },
                    formatSelection: function (info) {
                        return   info.nombre;
                    },
                    escapeMarkup: function (m) {
                        return m;
                    }
                };
            },
            upFirma: function (firma) {
                let vue = this;
                if (firma.orden < 2) {
                    return;
                }
                let oldOrder = parseInt(firma.orden);
                let newOrder = parseInt(firma.orden - 1);
                let firmaDocumento = vue.firmasDocumento.find(item => item.orden === newOrder);
                firmaDocumento.orden = oldOrder;
                firma.orden = newOrder;
                setTimeout(function () {
                    vue.updateSelect2();
                }, 50);
            },
            downFirma: function (firma) {
                let vue = this;
                if (firma.orden >= vue.firmasDocumento.length) {
                    return
                }
                let oldOrder = parseInt(firma.orden);
                let newOrder = parseInt(firma.orden + 1);
                let firmaDocumento = vue.firmasDocumento.find(item => item.orden === newOrder);
                firmaDocumento.orden = oldOrder;
                firma.orden = newOrder;
                setTimeout(function () {
                    vue.updateSelect2();
                }, 50);
            },
            reOrder: function (backOrder, max) {
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
    };
</script>