<template>
    <div>

        <section class="panel">
            <section class="panel-body">
                <raptor-table ref="dynatable"
                              v-bind:url="URL_COSTO_DOCUMENTO" >
                    <template scope="props">
                        <table class="table table-striped">
                            <thead>
                                <tr>
                                    <th class="col-md-6 text-left v-middle">Documento</th>
                                    <th class="col-md-4 text-left v-middle">Idioma</th>
                                    <th class="col-md-1 text-left v-middle">Precio</th>
                                    <th class="col-md-1 text-left v-middle">Costo por Ciclo</th>
                                    <th></th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr v-for="item in props.data"> 

                                    <td class="v-middle">
                                        <span class="block">{{item.tipoDocumento.nombre}}</span> 
                                    </td>

                                    <td class="v-middle ">
                                        <span> {{item.idioma.nombre}}</span> 
                                    </td>


                                    <td class="v-middle text-right">
                                        <span >{{item.precio}}</span> 
                                    </td>


                                    <td class="v-middle text-center ">

                                        <span v-if="item.tipoDocumento.costoCiclo" class="text-success"> <i class="fa fa-check fa-2x" aria-hidden="true"></i></span>
                                        <span v-else="" class="text-danger"> <i class="fa fa-times fa-2x" aria-hidden="true"></i></span>

                                    </td>

                                    <td class="v-middle "> 

                                        <div class="dropdown actions">

                                            <a class="dropdown-toggle" data-toggle="dropdown">
                                                <i class="fa fa-cog"></i>
                                            </a> 

                                            <ul class="dropdown-menu pull-right" >                
                                                <li><a href="#" v-on:click.prevent="modalUpdate(item)">Actualizar</a></li>
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
                URL_COSTO_DOCUMENTO: APP.url('tramite/costodocumento/all'),
            };
        },
        mounted: function () {
            let $vue = this;
        },
        methods: {
            modalUpdate(item) {
                this.$parent.update({...item});
            },
            reload() {
                this.$refs.dynatable.repreload();
            }
        }
    };
</script>