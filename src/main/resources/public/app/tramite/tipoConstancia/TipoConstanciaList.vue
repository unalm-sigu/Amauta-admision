<template>
    <div>

        <section class="panel">
            <section class="panel-body">


                <raptor-table ref="raptor"
                              v-bind:url="URL_TIPO_CONSTANCIA" >
                    <template scope="props">
                        <table class="table table-striped">
                            <thead>
                                <tr>
                                    <th class="text-center v-middle">configurado</th>
                                    <th class="v-middle  col-xs-7">Nombre</th>
                                    <th class="text-center  col-xs-1 v-middle">Costo por ciclo</th>
                                    <th class="text-center  col-xs-1 v-middle">egresado</th>
                                    <th class="text-center  col-xs-1 v-middle">Solo pregrado</th>
                                    <th class="text-center  col-xs-1 v-middle">Solo posgrado</th>
                                    <th class="text-center  col-xs-1 v-middle">Solo Especial</th>
                                    <th class="v-middle col-xs-1">Tipo</th>
                                    <th class="v-middle"></th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr v-for="(item, index ) in props.data"> 

                                    <td class="text-center v-middle">
                                        <i v-if="item.configurado==1" class="fa fa-2x fa-check text-success"></i>
                                        <i v-else="" class="fa fa-2x fa-times text-danger"></i>
                                    </td>

                                    <td class="v-middle">
                                        <p class="block h4 text-primary" v-text="item.nombre" ></p>
                                        <p class="block text-muted" v-text="item.codigo" ></p>
                                        <p class="block text-muted" v-text="item.oficinaEmisora.nombre" ></p>
                                    </td>

                                    <td class="v-middle text-center">
                                        <i v-if="item.costoCiclo=='1'" class="fa fa-2x fa-check text-success"></i>
                                        <i v-else="" class="fa fa-2x fa-times text-danger"></i>
                                    </td>

                                    <td class="v-middle text-center">
                                        <i v-if="item.requiereEgresado=='1'" class="fa fa-2x fa-check text-success"></i>
                                        <i v-else="" class="fa fa-2x fa-times text-danger"></i>
                                    </td>

                                    <td class="v-middle text-center">
                                        <i v-if="item.requierePregrado=='1'" class="fa fa-2x fa-check text-success"></i>
                                        <i v-else="" class="fa fa-2x fa-times text-danger"></i>
                                    </td>
                                    <td class="v-middle text-center">
                                        <i v-if="item.requierePosgrado=='1'" class="fa fa-2x fa-check text-success"></i>
                                        <i v-else="" class="fa fa-2x fa-times text-danger"></i>
                                    </td>
                                    <td class="v-middle text-center">
                                        <i v-if="item.requiereEspecial=='1'" class="fa fa-2x fa-check text-success"></i>
                                        <i v-else="" class="fa fa-2x fa-times text-danger"></i>
                                    </td>

                                    <td class="v-middle">
                                        <p class="bold text-warning" v-if="item.tipo=='CONS'">CONSTANCIA</p>
                                        <p class="bold text-success" v-if="item.tipo=='CERT'">CERTIFICADO</p>
                                    </td>
                                    <td class="v-middle">
                                        <div class="dropdown actions">
                                            <a class="dropdown-toggle" data-toggle="dropdown">
                                                <i class="fa fa-cog"></i>
                                            </a>
                                            <ul class="dropdown-menu pull-right" >
                                                <li><a href="#"  class="" v-on:click.prevent="updateTipo(item)">Actualizar</a></li>
                                                <li class="divider"></li>
                                                <li><a href="#"  class="" v-on:click.prevent="eliminar(item)">Eliminar</a></li>
                                            </ul>
                                        </div>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </template>
                </raptor-table>

            </section>
        </section>

    </div>
</template>

<script>
    module.exports = {
        components: {
            RaptorTable: use("/_vue/modules/RaptorTable.vue"),
        },
        data() {
            return {
                URL_TIPO_CONSTANCIA: APP.url('tramite/tipoconstancia/all'),
            };
        },
        mounted: function () {
            let $vue = this;
            $global.$on("RELOAD", function () {
                $vue.reload();
            });
        },
        methods: {
            updateTipo(item) {
                this.$parent.setTipoConstancia({...item});
                this.$parent.update();
            },
            eliminar(tipoConstancia) {
                var $vue = this;
                bootbox.confirm({
                    message: '¿Seguro que desea eliminar el tipo  de constancia?',
                    buttons: {
                        confirm: {label: 'Si, eliminar', className: "btn-danger"},
                        cancel: {label: 'Salir', className: "btn-link"}
                    },
                    callback: function (result) {
                        if (result) {
                            $.ajax({
                                method: 'POST',
                                url: APP.url('tramite/tipoconstancia/delete'),
                                data: {id: tipoConstancia.id},
                                success: function (response) {
                                    if (response.success) {
                                        notify(response.message, 'info');
                                        $vue.$refs.raptor.loadRemoteData();
                                    } else {
                                        notify(response.message, 'error');
                                    }
                                }, error: function () {
                                    notify(Messages.errorComunicacion, "error");
                                }
                            });
                        }
                    }
                });
            },
            reload() {
                var $vue = this;
                $vue.$refs.raptor.loadRemoteData();
            }
        }
    };
</script>