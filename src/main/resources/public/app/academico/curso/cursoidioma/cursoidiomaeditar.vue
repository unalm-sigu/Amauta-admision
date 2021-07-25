<template>
  <div >
      
    <div class="v-middle" v-if="editar">

      <textarea v-model="nombreCursoActivo.nombre" class="form-control"></textarea>

      <button v-if="nombreCursoActivo.id" style="margin-top:10px" v-on:click="actualizar" class="btn btn-primary btn-xs">Actualizar</button>

      <button v-else="" style="margin-top:10px" v-on:click="guardar" class="btn btn-primary btn-xs">Guardar</button>

      <button style="margin-top:10px" v-on:click="eliminar" class="btn btn-danger btn-xs">Eliminar</button>

      <button style="margin-top:10px" v-on:click="cancelar" class="btn btn-default btn-xs">Cancelar</button>

    </div>

    <div class="v-middle" v-else="">
      <p class="h4">{{ value.nombre }}</p>
    </div>

    <button
      v-if="!editar"
      v-on:click.prevent="editarNombreCurso"
      class="btn btn-link pull-right" >
      <i class="fa fa-pencil" aria-hidden="true"></i>
    </button>

  </div>
</template>

<script>
module.exports = {
    props: ['value'],
    data() {
        return {
            editar: false,
            nombreCursoActivo:{}
        };
    },
    methods: {
    editarNombreCurso () { 
        let $vue=this;
        $vue.editar=true;
        $vue.nombreCursoActivo={...$vue.value};
    },
    guardar () { 
        let $vue=this;
        axios.post(APP.url('academico/curso/idioma/'),$vue.nombreCursoActivo).
            then(({data}) => {
                if(data.success){
                    notify(data.message,'info');
                    $vue.$parent.$parent.$refs.raptorCursos.loadRemoteData();
                    $vue.editar=false;
                    $vue.nombreCursoActivo={...$vue.value};
                }else{
                    notify(data.message, "error");
                }
            }, error => {
                notify(Messages.errorComunicacion, "error");
            });
    },
    eliminar () {


        swal({
        title: "Seguro que desea eliminar el registro",
        icon: "warning",
        buttons: ["Cancelar", "Eliminar"],
        dangerMode: true,
        }).then((willDelete) => {
        if (willDelete) {

        let $vue=this;
        axios.delete(APP.url('academico/curso/idioma/'+ $vue.nombreCursoActivo.id)).
            then(({data}) => {
                if(data.success){
                    notify(data.message,'info');
                    $vue.$parent.$parent.$refs.raptorCursos.loadRemoteData();
                    $vue.editar=false;
                    $vue.nombreCursoActivo={...$vue.value};
                }else{
                    notify(data.message, "error");
                }
            }, error => {
                notify(Messages.errorComunicacion, "error");
            });

        }
        });


    },
    actualizar () { 
        let $vue=this;
        axios.put(APP.url('academico/curso/idioma/'),$vue.nombreCursoActivo).
            then(({data}) => {
                if(data.success){
                    notify(data.message,'info');
                    $vue.$parent.$parent.$refs.raptorCursos.loadRemoteData();
                    $vue.editar=false;
                    $vue.nombreCursoActivo={...$vue.value};
                }else{
                    notify(data.message, "error");
                }
            }, error => {
                notify(Messages.errorComunicacion, "error");
            });
    },
    cancelar () {
        let $vue=this;
        $vue.editar=false;
        $vue.nombreCursoActivo={...$vue.value};
    },
    }
};
</script>
