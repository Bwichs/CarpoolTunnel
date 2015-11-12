//
//  PinAnnotation.swift
//  ParseStarterProject-Swift
//
//  Created by Brian Wichers on 10/31/15.
//  Copyright © 2015 Parse. All rights reserved.
//

import MapKit
import Foundation
import UIKit

class PinAnnotation : NSObject, MKAnnotation {
    private var coord: CLLocationCoordinate2D
    var parseObjectID: String?
    
    init(pinCoord: CLLocationCoordinate2D, routeObjectID: String){
        self.coord = pinCoord
        self.parseObjectID = routeObjectID
    }
    
    var coordinate: CLLocationCoordinate2D {
        get {
            return coord
        }
    }
    
    var title: String? = ""
    var subtitle: String? = ""
    
    func setCoordinate(newCoordinate: CLLocationCoordinate2D) {
        self.coord = newCoordinate
    }
}