import ios_combined

final class SessionService {
    func convertSessionResponse(response: Response) -> [Session] {
        response.sessions.map { session -> Session in

            let room = response.rooms?.first(where: { room -> Bool in
                room.id == session.roomId
            })

            let speakers = response.speakers?.filter({ (speaker) -> Bool in
                session.speakers.contains(speaker.id ?? "")
            }) ?? []

            /// Single value?
            let categories = response.categories?.filter({ (category) -> Bool in
                guard let categoryId = category.id,
                    let sessionCategoryId = session.sessionCategoryItemId else {
                        return false
                }
                return categoryId == sessionCategoryId
            }) ?? []

            let startsAtString = session.startsAt ?? ""
            let endsAtString = session.endsAt ?? ""

            let dateFormatter = ISO8601DateFormatter()
            let startsAt = dateFormatter.date(from: startsAtString)
            let endsAt = dateFormatter.date(from: endsAtString)

            let minutes = (endsAt?.timeIntervalSince(startsAt!))
                .map { Int($0) / 60 }

            return Session(response: session, room: room, speakers: speakers, categories: categories, startsAt: startsAt, endsAt: endsAt, minutes: minutes)
        }
    }
}
