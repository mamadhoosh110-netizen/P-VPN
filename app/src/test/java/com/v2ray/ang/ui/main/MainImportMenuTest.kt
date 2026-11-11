package com.v2ray.ang.ui.main

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MainImportMenuTest {
    @Test
    fun regularServerMenuDoesNotExposeSharing() {
        assertEquals(emptyList<ServerMenuAction>(), serverMenuActions(false, false))
    }

    @Test
    fun regularManagementMenuContainsEditAndDeleteOnly() {
        assertEquals(listOf(ServerMenuAction.Edit, ServerMenuAction.Delete), serverMenuActions(false, true))
    }

    @Test
    fun complexManagementMenuContainsEditAndDeleteOnly() {
        assertEquals(listOf(ServerMenuAction.Edit, ServerMenuAction.Delete), serverMenuActions(true, true))
    }
}
