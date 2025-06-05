package com.loadingbyte.credgen.project

import kotlinx.collections.immutable.PersistentList


class Project(
    val styling: Styling,
    val credits: PersistentList<Credits>
)
