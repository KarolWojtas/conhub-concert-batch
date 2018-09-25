package com.karol.conhubconcertbatch.domain

import org.springframework.data.mongodb.repository.MongoRepository

interface ConcertRepository : MongoRepository<Concert, String>

interface VenueRepository : MongoRepository<Venue, String>