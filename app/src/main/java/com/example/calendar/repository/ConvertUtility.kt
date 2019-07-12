package com.example.calendar.repository

import com.example.calendar.repository.db.convert.DurationConverter
import com.example.calendar.repository.db.convert.ZoneIdConverter
import com.example.calendar.repository.db.convert.ZonedDateTimeConverter

val duration_cn = DurationConverter()
val zonedDateTime_cn = ZonedDateTimeConverter()
val zoneId_cn = ZoneIdConverter()