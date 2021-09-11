<template>
    <div>

        <section class="panel">
            <section class="panel-body">


                <raptor-table ref="dynaTable"
                              v-bind:url="URL_TIPO_CONSTANCIA" >
                    <template scope="props">
                        <table class="table table-striped">
                            <thead>
                                <tr>
                                    <th></th>
                                    <th class="v-middle  col-xs-5">Nombre</th>
                                    <th class="text-center  col-xs-1 v-middle">Costo por ciclo</th>
                                    <th class="text-center  col-xs-1 v-middle">Solo pregrado</th>
                                    <th class="text-center  col-xs-1 v-middle">Solo posgrado</th>
                                    <th class="text-center  col-xs-1 v-middle">Solo egresado</th>
                                    <th class="v-middle col-xs-2">Tipo</th>
                                    <th class="text-center  col-xs-1">configurado</th>
                                    <th class="v-middle"></th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr v-for="(item, index ) in props.data"> 
                                    <td class="v-middle">
                                        {{index+1}}
                                    </td>
                                    <td class="v-middle">
                                        <p v-text="item.nombre" ></p>
                                    </td>
                                    <td class="v-middle text-center">
                                        <i v-if="item.costoCiclo=='1'" class="fa fa-2x fa-check text-success"></i>
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
                                        <i v-if="item.requiereEgresado=='1'" class="fa fa-2x fa-check text-success"></i>
                                        <i v-else="" class="fa fa-2x fa-times text-danger"></i>
                                    </td>

                                    <td class="v-middle">
                                        <p v-if="item.tipoConstancia=='CONS'">Constancia</p>
                                        <p v-else="item.tipoConstancia=='CERT'">Certificado</p>
                                    </td>
                                    <td class="text-center">
                                        <i v-if="item.configurado==1" class="fa fa-2x fa-check text-success"></i>
                                        <i v-else="" class="fa fa-2x fa-times text-danger"></i>
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
        data() {
            return {
                URL_TIPO_CONSTANCIA: APP.url('tramite/tipoconstancia/all'),
            };
        },
        mounted: function () {
            let $vue = this;
        },
        methods: {
            updateTipo(item) {
                this.$parent.setTipoConstancia({...item});
                this.$parent.update();
            },
            eliminar(tipoConstancia) {
                var vue = this;
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
                                        vue.$refs.dynaTable.repreload();
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
                this.$refs.dynaTable.repreload();
            }
        }
    };
</script>