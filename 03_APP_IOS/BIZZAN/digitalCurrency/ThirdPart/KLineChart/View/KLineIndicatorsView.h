//
//  KLineIndicatorsView.h
//  KLine-Chart-OC
//
//  Created by 何俊松 on 2020/3/10.
//  Copyright © 2020 hjs. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface KLineIndicatorsView : UIView
+(KLineIndicatorsView *)indicatorsView;
-(void)correctState;
@end

NS_ASSUME_NONNULL_END
