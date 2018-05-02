package `arrow`.`ap`.`objects`

import arrow.core.left
import arrow.core.right
import arrow.core.toOption

fun optionalNullable(): arrow.optics.Optional<`arrow`.`ap`.`objects`.`Optional`, `kotlin`.`String`> = arrow.optics.Optional(
  getOrModify = { optional: `arrow`.`ap`.`objects`.`Optional` -> optional.`nullable`?.right() ?: optional.left() },
  set = { value: `kotlin`.`String` ->
    { optional: `arrow`.`ap`.`objects`.`Optional` ->
      optional.copy(`nullable` = value)
    }
  }
)
fun optionalOption(): arrow.optics.Optional<`arrow`.`ap`.`objects`.`Optional`, `kotlin`.`String`> = arrow.optics.Optional(
  getOrModify = { optional: `arrow`.`ap`.`objects`.`Optional` -> optional.`option`.orNull()?.right() ?: optional.left() },
  set = { value: `kotlin`.`String` ->
    { optional: `arrow`.`ap`.`objects`.`Optional` ->
      optional.copy(`option` = value.toOption())
    }
  }
)