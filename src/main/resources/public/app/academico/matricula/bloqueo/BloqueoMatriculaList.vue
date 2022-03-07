<template>
    <div>

        <section class="panel">
            <section class="panel-body">
                <raptor-table ref="dynatable"
                              v-bind:url="URL_BLOQUEO_MATRICULA" >
                    <template scope="props">
                        <table class="table table-striped">
                            <thead>
                                <tr>
                                    <th class="col-md-6 text-left v-middle">Ciclo</th>
                                    <th class="col-md-4 text-left v-middle">Carrera</th>
                                    <th class="col-md-1 text-left v-middle">Situación Académica</th>
                                    <th></th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr v-for="item in props.data"> 

                                    <td class="v-middle">
                                        <span class="block text-primary">{{item.cicloAplica.descripcion}}</span> 
                                    </td>

                                    <td class="v-middle ">
                                        <span> {{item.carrera.nombre}}</span> 
                                    </td>

                                    <td class="v-middle ">
                                        <span> {{item.situacionAcademica.nombre}}</span> 
                                    </td>

                                    <td class="v-middle "> 

                                        <div class="dropdown actions">

                                            <a class="dropdown-toggle" data-toggle="dropdown">
                                                <i class="fa fa-cog"></i>
                                            </a> 

                                            <ul class="dropdown-menu pull-right" >                
                                                <!--<li><a href="#" v-on:click.prevent="update(item)">Actualizar</a></li>-->
                                                <li role="presentation" class="divider"></li>
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
        data() {
            return {
                URL_BLOQUEO_MATRICULA: APP.url('academico/matricula/bloqueo/all'),
            };
        },
        mounted: function () {
            let $vue = this;
        },
        methods: {
            update(item) {
                this.$parent.update({...item});
            },
            eliminar(item) {
                let $vue = this;
                swal('¿Seguro que desea eliminar el registro?', {
                    icon: "warning",
                    closeOnClickOutside: false,
                    closeOnEsc: false,
                    dangerMode: true,
                    buttons: {
                        cancel: {text: "Cancelar", closeModal: true, visible: true},
                        confirm: {text: "Sí, Eliminar", closeModal: false}
                    }
                }).then((value) => {
                    if (value != true) {
                        return;
                    }
                    axios_.delete("/academico/matricula/bloqueo/" + item.id)
                            .then(({data}) => {
                                $vue.reload();
                                notify(data, 'info');
                                return swal({text: data, icon: "success", button: false, timer: 1000});
                            }, () => {
                                return swal(APP.errorComunicacion, "error");
                            });

                }).catch(err => {
                    if (err) {
                        swal(APP.errorComunicacion, "error");
                    } else {
                        swal.stopLoading();
                        swal.close();
                    }
                });

            },
            reload() {
                this.$refs.dynatable.repreload();
            }
        }
    };
</script>