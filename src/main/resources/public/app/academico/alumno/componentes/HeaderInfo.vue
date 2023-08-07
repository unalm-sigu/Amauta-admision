<template>
    <div>
        <div class="text-primary h3 m-t m-b-xs" >
            {{alumno.codigo}} - {{alumno.persona.apellidosNombres}}
        </div>
    </div>

</template>
<script>
    module.exports = {
        props: {
            alumno: {}
        },

        methods: {
        }
    };
</script>