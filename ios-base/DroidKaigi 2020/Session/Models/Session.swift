import ios_combined

struct Session {
    let response: SessionResponse
    let room: RoomResponse?
    let speakers: [SpeakerResponse]
    let categories: [CategoryResponse]

    let startsAt: Date?
    let endsAt: Date?
    let minutes: Int?
}
