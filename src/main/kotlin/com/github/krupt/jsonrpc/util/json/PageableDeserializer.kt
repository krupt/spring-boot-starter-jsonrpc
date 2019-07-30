package com.github.krupt.jsonrpc.util.json

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.ObjectCodec
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties
import org.springframework.boot.jackson.JsonComponent
import org.springframework.boot.jackson.JsonObjectDeserializer
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

@JsonComponent
@ConditionalOnClass(Pageable::class)
class PageableDeserializer(
        springDataWebProperties: SpringDataWebProperties
) : JsonObjectDeserializer<Pageable>() {

    private val pageableProperties = springDataWebProperties.pageable

    override fun deserializeObject(jsonParser: JsonParser?, context: DeserializationContext?, codec: ObjectCodec?, tree: JsonNode?): Pageable? {
        if (tree?.isObject == true) {
            val pageNumber = tree.get("page")?.asInt() ?: 0
            val pageSize = tree.get("size")?.asInt() ?: pageableProperties.defaultPageSize
            val sort = tree.get("sort")

            return PageRequest.of(
                    pageNumber,
                    if (pageSize > pageableProperties.maxPageSize)
                        pageableProperties.maxPageSize
                    else
                        pageSize,
                    deserializeSort(jsonParser, sort)
            )
        } else {
            throw MismatchedInputException.from(jsonParser, Pageable::class.java, "Cannot construct instance of `${Pageable::class.java.name}` from non-object value")
        }
    }

    private fun deserializeSort(jsonParser: JsonParser?, sortNode: JsonNode?) =
            if (sortNode?.isArray == true)
                Sort.by(
                        sortNode.mapNotNull {
                            val direction = Sort.Direction.valueOf(it.get("direction")?.asText() ?: "ASC")
                            it.get("property")?.asText()?.let { property ->
                                Sort.Order(direction, property)
                            }
                        }
                )
            else if (sortNode == null || sortNode.isNull)
                Sort.unsorted()
            else
                throw JsonMappingException(jsonParser, "Property 'sort' has value that is not of type ArrayNode (but ${sortNode::class.java.name})")
}
