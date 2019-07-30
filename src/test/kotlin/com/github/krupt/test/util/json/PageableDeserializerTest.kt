package com.github.krupt.test.util.json

import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.fasterxml.jackson.module.kotlin.readValue
import io.kotlintest.matchers.string.shouldStartWith
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

@SpringBootTest
internal class PageableDeserializerTest {

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `deserialize`() {
        val pageable = objectMapper.readValue<Pageable>(
                """{
                    "page": 1,
                    "size": 50
                }""".trimIndent()
        )

        pageable.pageNumber shouldBe 1
        pageable.pageSize shouldBe 50
        pageable.sort shouldBe Sort.unsorted()
    }

    @Test
    fun `deserialize from null`() {
        val pageable = objectMapper.readValue<Pageable>(
                "null"
        )

        pageable shouldBe null
    }

    @Test
    fun `deserialize from empty object`() {
        val pageable = objectMapper.readValue<Pageable>(
                "{}"
        )

        pageable.pageNumber shouldBe 0
        pageable.pageSize shouldBe 20
        pageable.sort shouldBe Sort.unsorted()
    }

    @Test
    fun `deserialize without page size`() {
        val pageable = objectMapper.readValue<Pageable>(
                """{
                    "page": 1
                }""".trimIndent()
        )

        pageable.pageNumber shouldBe 1
        pageable.pageSize shouldBe 20
        pageable.sort shouldBe Sort.unsorted()
    }

    @Test
    fun `deserialize with size overriding`() {
        val pageable = objectMapper.readValue<Pageable>(
                """{
                    "size": 10000
                }""".trimIndent()
        )

        pageable.pageNumber shouldBe 0
        pageable.pageSize shouldBe 2000
        pageable.sort shouldBe Sort.unsorted()
    }

    @Test
    fun `deserialization error with string`() {
        assertThrows<MismatchedInputException> {
            objectMapper.readValue<Pageable>(
                    "\"hello\""
            )
        }.message shouldBe "Cannot construct instance of `${Pageable::class.java.name}` from non-object value\n" +
                " at [Source: (String)\"\"hello\"\"; line: 1, column: 1]"
    }

    @Test
    fun `deserialization error with array`() {
        assertThrows<MismatchedInputException> {
            objectMapper.readValue<Pageable>(
                    "[\"hello\"]"
            )
        }.message shouldBe "Cannot construct instance of `${Pageable::class.java.name}` from non-object value\n" +
                " at [Source: (String)\"[\"hello\"]\"; line: 1, column: 9]"
    }

    @Test
    fun `deserialize sort`() {
        val pageable = objectMapper.readValue<Pageable>(
                """{
                    "sort": [
                        {
                            "property": "name",
                            "direction": "DESC"
                        },
                        {
                            "property": "date",
                            "direction": "DESC"
                        },
                        {
                            "property": "amount"
                        }
                    ]
                }""".trimIndent()
        )

        pageable.pageNumber shouldBe 0
        pageable.pageSize shouldBe 20
        pageable.sort shouldBe Sort.by(Sort.Direction.DESC, "name", "date")
                .and(Sort.by("amount"))
    }

    @Test
    fun `deserialize empty sort`() {
        val pageable = objectMapper.readValue<Pageable>(
                """{
                    "sort": [
                        {
                        }
                    ]
                }""".trimIndent()
        )

        pageable.pageNumber shouldBe 0
        pageable.pageSize shouldBe 20
        pageable.sort shouldBe Sort.unsorted()
    }

    @Test
    fun `sort deserialization error with boolean`() {
        assertThrows<JsonMappingException> {
            objectMapper.readValue<Pageable>(
                    """{
                    "sort": true
                }""".trimIndent()
            )
        }.message shouldStartWith "Property 'sort' has value that is not of type ArrayNode (but com.fasterxml.jackson.databind.node.BooleanNode)"
    }

    @Test
    fun `deserialize sort with empty array`() {
        val pageable = objectMapper.readValue<Pageable>(
                """{
                    "sort": []
                }""".trimIndent()
        )

        pageable.pageNumber shouldBe 0
        pageable.pageSize shouldBe 20
        pageable.sort shouldBe Sort.unsorted()
    }

    @Test
    fun `deserialization error with unknown direction`() {
        assertThrows<Exception> {
            objectMapper.readValue<Pageable>(
                    """{
                    "sort": [
                        {
                            "direction": "UNKNOWN"
                        }
                    ]
                }""".trimIndent()
            )
        }.cause?.message shouldBe "No enum constant org.springframework.data.domain.Sort.Direction.UNKNOWN"
    }

    @Test
    fun `deserialize sort with null`() {
        val pageable = objectMapper.readValue<Pageable>(
                """{
                    "sort": null
                }""".trimIndent()
        )

        pageable.pageNumber shouldBe 0
        pageable.pageSize shouldBe 20
        pageable.sort shouldBe Sort.unsorted()
    }
}
