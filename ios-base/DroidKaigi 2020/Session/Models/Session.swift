//
//  Session.swift
//  DroidKaigi 2020
//
//  Created by 伊藤凌也 on 2020/01/18.
//

import api

struct Session {
    let response: SessionResponse
    let room: RoomResponse?
    let speakers: [SpeakerResponse]
    let categories: [CategoryResponse]

    let startsAt: Date?
    let endsAt: Date?
    let minutes: Int?
}
