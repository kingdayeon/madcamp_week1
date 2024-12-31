data class TypeListResponse(
    val pokemon: List<PokemonListEntry>
)

data class PokemonListEntry(
    val pokemon: Pokemon
)

data class Pokemon(
    val name: String,
    val url: String
)