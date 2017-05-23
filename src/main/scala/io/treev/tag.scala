package io.treev

/**
  * Adapted from
  * https://github.com/softwaremill/scala-common/blob/master/tagging/src/main/scala/com/softwaremill/tagging/package.scala
  * with added tagging operators, function-first-style tagging, and explicit container-types tagging.
  */
object tag {

  type Tag[+U] = { type Tag <: U }
  type Tagged[+T, +U] = T with Tag[U]
  type @@[+T, +U] = Tagged[T, U]

  /** Base tagged type trait.
    * @tparam R raw value type */
  trait TaggedType[R] {
    /** Tagged value tag. */
    sealed trait Tag

    /** Raw value type. */
    type Raw = R
    /** Tagged value type. */
    type Type = Raw @@ Tag

    /** Create tagged value from raw value. */
    def apply(raw: Raw): Type = raw.@@[Tag]
  }

  /** Function-first-style tagging API.
    * @tparam U type to tag with
    * @return `Tagger` instance that can be used for tagging */
  def apply[T, U](t: T): T @@ U = t.asInstanceOf[T @@ U]

  implicit class TaggingExtensions[T](val t: T) extends AnyVal {

    /** Tag with type `U`.
      * @tparam U type to tag with
      * @return value tagged with `U` */
    def taggedWith[U]: T @@ U = t.asInstanceOf[T @@ U]
    /** Synonym operator for `taggedWith`. */
    def @@[U]: T @@ U = taggedWith[U]

    /** Tag using `TaggedType` instance.
      * Allows for `String @@ MyTaggedType` syntax.
      * @param taggedType tagged type to type-tag with using its inner `Tag` field
      * @return value tagged with `taggedType.Tag` */
    def taggedWith(taggedType: TaggedType[_]): T @@ taggedType.Tag = taggedWith[taggedType.Tag]
    /** Synonym operator for `taggedWith`. */
    def @@(taggedType: TaggedType[_]): T @@ taggedType.Tag = taggedWith[taggedType.Tag]

  }
  implicit class AndTaggingExtensions[T, U](val t: T @@ U) extends AnyVal {

    /** Tag tagged value with type `V`.
      * @tparam V type to tag with
      * @return value tagged with both `U` and `V` */
    def andTaggedWith[V]: T @@ (U with V) = t.asInstanceOf[T @@ (U with V)]
    /** Synonym operator for `andTaggedWith`. */
    def +@[V]: T @@ (U with V) = andTaggedWith[V]

    /** Tag tagged value using `TaggedType` instance.
      * Allows for `String +@ MyTaggedType` syntax.
      * @param taggedType tagged type to type-tag with using its inner `Tag` field
      * @return value tagged with both `U` and `taggedType.Tag` */
    def andTaggedWith(taggedType: TaggedType[_]): T @@ (U with taggedType.Tag) = andTaggedWith[taggedType.Tag]
    /** Synonym operator for `andTaggedWith`. */
    def +@(taggedType: TaggedType[_]): T @@ (U with taggedType.Tag) = andTaggedWith[taggedType.Tag]

  }

  implicit class TaggingExtensionsF[F[_], T](val ft: F[T]) extends AnyVal {

    /** Tag intra-container values with type `U`.
      * @tparam U type to tag with
      * @return container with nested values tagged with `U` */
    def taggedWithF[U]: F[T @@ U] = ft.asInstanceOf[F[T @@ U]]
    /** Synonym operator for `taggedWithF`. */
    def @@@[U]: F[T @@ U] = taggedWithF[U]

    /** Tag intra-container values using `TaggedType` instance.
      * Allows for `List(...) @@ MyTaggedType` syntax.
      * @param taggedType tagged type to type-tag with using its inner `Tag` field
      * @return container with nested values tagged with `taggedType.Tag` */
    def taggedWithF(taggedType: TaggedType[_]): F[T @@ taggedType.Tag] = taggedWithF[taggedType.Tag]
    /** Synonym operator for `taggedWith`. */
    def @@@(taggedType: TaggedType[_]): F[T @@ taggedType.Tag] = taggedWithF[taggedType.Tag]

  }
  implicit class AndTaggingExtensionsF[F[_], T, U](val ft: F[T @@ U]) extends AnyVal {

    /** Tag tagged intra-container values with type `U`.
      * @tparam V type to tag with
      * @return container with nested values tagged with both `U` and `V` */
    def andTaggedWithF[V]: F[T @@ (U with V)] = ft.asInstanceOf[F[T @@ (U with V)]]
    /** Synonym operator for `andTaggedWithF`. */
    def +@@[V]: F[T @@ (U with V)] = andTaggedWithF[V]

    /** Tag tagged intra-container values using `TaggedType` instance.
      * Allows for `List(...) +@@ MyTaggedType` syntax.
      * @param taggedType tagged type to type-tag with using its inner `Tag` field
      * @return container with nested values tagged with both `U` and `taggedType.Tag` */
    def andTaggedWithF(taggedType: TaggedType[_]): F[T @@ (U with taggedType.Tag)] = andTaggedWithF[taggedType.Tag]
    /** Synonym operator for `andTaggedWithF`. */
    def +@@(taggedType: TaggedType[_]): F[T @@ (U with taggedType.Tag)] = andTaggedWithF[taggedType.Tag]

  }

}
