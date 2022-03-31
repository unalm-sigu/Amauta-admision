<template>
    <div>
        <section class="panel">
            <section class="panel-body">

                <raptor-table v-bind:url="historialURL" v-bind:preload="false" ref="raptor">

                    <div slot="header"></div>

                    <template scope="props" >
                        <table class="table table-striped table-hover">
                            <thead> 
                                <tr>
                                    <th class="col-md-3 text-center">Ciclo Académico</th>
                                    <th class="col-md-3 text-center v-middle">Fecha Creación</th>
                                    <th class="col-md-3 text-center v-middle">Fecha Actualización</th>
                                    <th class="col-md-1 text-center v-middle">Estado</th>
                                    <th></th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr v-for="item in props.data">
                                    <td class="v-middle">
                                        <a class="block text-primary bold h5 m-b-xs m-t-xs" >
                                            {{item.cicloAcademico.descripcion}}
                                        </a>
                                    </td>

                                    <td class="v-middle text-center">
                                        {{item.fechaCreacion}}
                                    </td>

                                    <td class="v-middle text-center">
                                        {{item.fechaActualizacion}}
                                    </td>

                                    <td class="v-middle text-center">

                                        <span v-if="item.estado=='ACTIVO'" class="label label-success" >{{item.estado}} </span>
                                        <span v-if="item.estado=='ELIMINADO'" class="label label-danger" >{{item.estado}} </span>

                                    </td>

                                    <td class="v-middle">
                                        <div class="actions">
                                            <a class="dropdown-toggle" href="#" data-toggle="dropdown"><i class="fa fa-cog"></i></a>
                                            <ul class="dropdown-menu pull-right">
                                                <li><a href="#" v-on:click.prevent="eliminar(item)">Eliminar</a></li>
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
                historialURL: APP.url('consejeria/administracion/all'),
                estado: {ACTIVO: "success", ANULADO: "danger"}
            };
        },
        mounted: function () {
            let $vue = this;
            $vue.$refs.raptor.repreload();
        },
        methods: {
            eliminar(item) {

                swal({
                    title: "Seguro que desea eliminar el registro",
                    icon: "warning",
                    buttons: ["Cancelar", "Eliminar"],
                    dangerMode: true,
                }).then((willDelete) => {
                    if (willDelete) {
                        let $vue = this;
                        axios.delete(APP.url('consejeria/administracion/eliminar/' + item.id)).
                                then(({data}) => {
                                    notify(data, 'info');
                                    $vue.$refs.raptor.loadRemoteData();
                                }, () => {
                                });

                    }
                });

            },
            reloadList() {
                let $vue = this;
                $vue.$refs.raptor.loadRemoteData();
            }
        }
    };
</script>