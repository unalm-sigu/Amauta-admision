<template>
    <div class="panel-body">
        <button class="btn btn-primary btn-sm pull-right m-b-xs"> + Agregar Idioma</button>
        <h3 class="page-header"> Idiomas </h3>

        <table class="table table-striped">
            <thead>
                <tr>
                    <th class="text-center">Idioma</th>
                    <th class="text-center">Nivel Conversación</th>
                    <th class="text-center">Nivel Lectura</th>
                    <th class="text-center">Nivel Escritura</th>
                    <th class="text-center">Materno</th>
                    <th></th>
                </tr>
            </thead>
            <tbody v-for="item in idiomasEscalafon"  class="editor">
                <tr>
                    <td class="text-center">
                        {{item.idioma}}
                    </td>
                    <td class="text-center">
                        {{item.conversa}}
                    </td>
                    <td class="text-center">
                        {{item.lectura}}
                    </td>
                    <td class="text-center">
                        {{item.escritura}}
                    </td>
                    <td class="text-center">
                        {{item.materno}}
                    </td>
                    <td class="text-center">
                        <button class=" btn-link">
                            <i class="fa fa-cog"></i>
                        </button>
                    </td>
                </tr>
            </tbody>
        </table>
        
        <idioma-form></idioma-form>

    </div>
</template>
<script>
    const IdiomaForm = httpVueLoader(package + '/idioma/IdiomaEscalafonForm.vue');
    
    module.exports = {
        components: {IdiomaForm},
        data() {
            return{
                idiomasEscalafon: [
                    {idioma: 'Inglés', conversa: 'Avanzado', lectura: 'Intermedio', escritura: 'Básico', materno: false},
                    {idioma: 'Portugues', conversa: 'Avanzado', lectura: 'Intermedio', escritura: 'Básico', materno: false},
                    {idioma: 'Frances', conversa: 'Avanzado', lectura: 'Avanzado', escritura: 'Avanzado', materno: true}
                ],
                idiomas: ['Inglés', 'Frances', 'Portugues']
            }
        }
    }
</script>
