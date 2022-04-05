<template>
    <div>
        <section class="panel">
            <section class="panel-body">

                <raptor-table v-bind:url="URL_LIST_ALUMNO" ref="load">
                    <div slot="header">
                    </div>
                    <template scope="props" >
                        <table class="table table-striped table-hover">
                            <thead> 
                                <tr>
                                    <th class="col-md-3 v-middle" colspan="2"></th>                    
                                    <th class="col-md-2 v-middle"></th>
                                    <th class="col-md-2 text-center v-middle"><span></span></th>
                                    <th class="col-md-2 text-center v-middle"><span></span></th>
                                    <th class="col-md-1 text-center v-middle"></th>   
                                    <th class="col-md-1 text-center v-middle"></th>   
                                    <th class="col-md-1 text-center v-middle">Estado</th>                    
                                    <th></th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr v-for="item in props.data">
                                    <td class="v-middle">
                                        <div class="pull-left">
                                            <img class="img-foto-tempo img-responsive img-thumbnail img-circle" v-bind:src="item.persona.rutaFoto" />
                                        </div>
                                    </td>
                                    <td class="v-middle">

                                        <a href="#" class="block text-primary bold h5 m-b-xs m-t-xs" >
                                            {{item.persona.apellidosNombres}}
                                        </a>
                                        <span class="block">
                                            Matrícula: {{item.codigo}}
                                        </span>
                                        <small v-if="item.persona.numeroDocIdentidad != '' " class="block bold">
                                            {{item.persona.tipoDocumento.simbolo}} {{item.persona.numeroDocIdentidad}}
                                        </small>

                                    </td>
                                    <td class="v-middle">
                                    </td>
                                    <td class="text-center v-middle">
                                    </td>
                                    <td class="text-center v-middle">
                                    </td>
                                    <td class="text-center v-middle">
                                    </td>
                                    <td class="text-center v-middle">
                                    </td>
                                    <td class="text-center v-middle">
                                    </td>
                                    <td class="v-middle">
                                        <div class="actions">
                                            <a class="dropdown-toggle" href="#" data-toggle="dropdown"><i class="fa fa-cog"></i></a>
                                            <ul class="dropdown-menu pull-right" >
                                                <li role="presentation">
                                                    <a class="btn-link estado" v-on:click.prevent='apply(item)' href="#">Aplicar Renuncia</a>
                                                </li>
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

    const RaptorTable = use("/_vue/modules/RaptorTable.vue");

    module.exports = {
        components: {
            RaptorTable,
        },
        data() {
            return {
                URL_LIST_ALUMNO: APP.url('academico/renuncia/alumno/all')
            };
        },
        methods: {
            apply() {

            }
        }
    };
</script>