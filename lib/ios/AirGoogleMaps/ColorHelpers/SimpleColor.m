//
//  SimpleColor.m
//  react-native-google-maps
//
//  Created by Reinert Lemmens on 08/08/2018.
//

#import "SimpleColor.h"

@implementation SimpleColor

-(id)initWith:(UInt8)r g:(UInt8)g b:(UInt8)b {
    self = [super init];
    if (self) {
        self.r = r;
        self.g = g;
        self.b = b;
    }
    return self;
}

+ (SimpleColor *)makeWithR:(UInt8)r g:(UInt8)g b:(UInt8)b {
    SimpleColor *color = [[SimpleColor alloc] init];
    if (color) {
        color.r = r;
        color.g = g;
        color.b = b;
    }
    return color;
}

@end
